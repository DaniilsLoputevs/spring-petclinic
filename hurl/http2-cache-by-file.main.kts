@file:DependsOn("org.apache.httpcomponents.client5:httpclient5:5.4")
@file:DependsOn("org.apache.httpcomponents.client5:httpclient5-cache:5.4")
//@file:DependsOn("org.ehcache:ehcache:3.10.0")

import org.apache.hc.client5.http.async.HttpAsyncClient
import org.apache.hc.client5.http.async.methods.*
import org.apache.hc.client5.http.config.TlsConfig
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient
import org.apache.hc.client5.http.impl.async.HttpAsyncClients
import org.apache.hc.client5.http.impl.async.MinimalHttpAsyncClient
import org.apache.hc.client5.http.impl.cache.CacheConfig
import org.apache.hc.client5.http.impl.cache.CachingH2AsyncClientBuilder
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder
import org.apache.hc.core5.concurrent.FutureCallback
import org.apache.hc.core5.http.HttpHeaders
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.config.Http1Config
import org.apache.hc.core5.http.message.StatusLine
import org.apache.hc.core5.http.nio.AsyncClientEndpoint
import org.apache.hc.core5.http2.HttpVersionPolicy
import org.apache.hc.core5.http2.config.H2Config
import org.apache.hc.core5.io.CloseMode
import org.apache.hc.core5.reactor.IOReactorConfig
import java.io.File
import java.util.concurrent.*
import kotlin.time.DurationUnit
import kotlin.time.measureTime

val etags = ConcurrentHashMap<String, String>()
val target = HttpHost("http", "localhost", 8000)
//val target = HttpHost("http", "89.169.140.137", 8000)
//val target = HttpHost("http", "89.169.154.40", 8080)
//val target = HttpHost("http", "cdn.u-dya.ru", 80)
val countInt = 100L
val minBound = 1
val maxBound = 50000

class AsyncClientH2Multiplexing {

    @Throws(Exception::class)
    fun oneByOne(client: CloseableHttpAsyncClient, requestUris: List<String>, allowEtag: Boolean) {

        try {
            val latch = CountDownLatch(requestUris.size)
            for (requestUri in (requestUris)) {
                val requestBuilder = SimpleRequestBuilder.get()
                    .setHttpHost(target)
                    .setPath(requestUri)
                    .addHeader(HttpHeaders.ACCEPT, "application/json")
                    .addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip")

                val etag = etags[requestUri]
                if (allowEtag && etag != null) {
                    requestBuilder.addHeader(HttpHeaders.IF_NONE_MATCH, etag)
                }

                val request = requestBuilder.build()

                client.execute(
                    SimpleRequestProducer.create(request),
                    SimpleResponseConsumer.create(),
                    object : FutureCallback<SimpleHttpResponse?> {
                        override fun completed(response: SimpleHttpResponse?) {
                            latch.countDown()

                            if (StatusLine(response).statusCode == 200) {
//                                println("################################# 200")
                                etags.putIfAbsent(requestUri, response?.getFirstHeader(HttpHeaders.ETAG)?.value ?: "")
                            }
                        }

                        override fun failed(ex: Exception) {
                            latch.countDown()
//                            println(request.toString() + "->" + ex)
                        }

                        override fun cancelled() {
                            latch.countDown()
//                            println(request.toString() + " cancelled")
                        }
                    })

            }
            latch.await()
        } finally {
//            endpoint.releaseAndReuse()
        }
    }
}

var threadCount = 8
var tp = Executors.newFixedThreadPool(threadCount)

val fLines = File("hurl/testdata/test_data_1_50").readLines()

var tasks = (1..threadCount).map {
    val delay = ThreadLocalRandom.current().nextLong(100, 60_00)
    Callable {

        Thread.sleep(delay)

        val cacheConfig = CacheConfig
            .custom()
            .setMaxObjectSize(500000) // 500KB
            .setMaxCacheEntries(200000 * 100)
            // Set this to false and a response with queryString
            // will be cached when it is explicitly cacheable .setNeverCacheHTTP10ResponsesWithQueryString(false)
            .build();


        val client = CachingH2AsyncClientBuilder.create()
            .setCacheConfig(cacheConfig).build()

        client.start()


//val measureTime = measureTime {
//        println(fLines.stream().skip(1).limit(50_000).toList().size)
        fLines.stream().skip(1).limit(500_000).toList()
            .shuffled().forEach { ch ->
            println(measureTime {
                val requestUris = ch.split(",").map { "/rest/owners/$it" }
//            val requestUris = listOf("/rest/owners/by-ids?ids=$ch")
//            println(requestUris)
                AsyncClientH2Multiplexing().oneByOne(client, requestUris, true)
            }.toLong(DurationUnit.MILLISECONDS))
//        Thread.sleep(100);
        }
//}

//println("First time (Gzip): $measureTime")

//(0..1000).forEach {
////    val secondTime = measureTime {
////        AsyncClientH2Multiplexing().oneByOne(client, true)
////    }
//
////    println("Second time (Gzip): " + measureTime {
////        AsyncClientH2Multiplexing().oneByOne(client, false)
////    })
//
//    println("Second time (Gzip + ETag): " + measureTime {
//        AsyncClientH2Multiplexing().oneByOne(client, true)
//    })
//
////    if (it % 8 == 0) {
//        println("ByIds (ETag): " + measureTime {
//            AsyncClientH2Multiplexing().getMany(client)
//        })
////    }


//    println("Waiting for 10 seconds...")
//
//    Thread.sleep(10)
//}


        client.close(CloseMode.GRACEFUL)
    }
}

var invokeAll = tp.invokeAll(tasks)
//invokeAll.forEach { it.get() }

Thread.sleep(6 * 60 * 60 * 1000)
