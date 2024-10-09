@file:DependsOn("org.apache.httpcomponents.client5:httpclient5:5.4")
@file:DependsOn("org.apache.httpcomponents.client5:httpclient5-cache:5.4")
//@file:DependsOn("org.ehcache:ehcache:3.10.0")

import org.apache.hc.client5.http.async.HttpAsyncClient
import org.apache.hc.client5.http.async.methods.*
import org.apache.hc.client5.http.cache.HttpCacheContext
import org.apache.hc.client5.http.cache.HttpCacheStorage
import org.apache.hc.client5.http.config.TlsConfig
import org.apache.hc.client5.http.impl.async.HttpAsyncClients
import org.apache.hc.client5.http.impl.async.MinimalHttpAsyncClient
import org.apache.hc.client5.http.impl.cache.CacheConfig
import org.apache.hc.client5.http.impl.cache.CachingH2AsyncClientBuilder
import org.apache.hc.client5.http.impl.cache.CachingHttpAsyncClientBuilder
import org.apache.hc.client5.http.impl.cache.CachingHttpAsyncClients
import org.apache.hc.client5.http.impl.cache.CachingHttpClients
import org.apache.hc.client5.http.impl.cache.ehcache.EhcacheHttpCacheStorage
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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import kotlin.streams.toList
import kotlin.time.measureTime

System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");

val etags = mutableMapOf<String, String>()
val target = HttpHost("http", "localhost", 8080)
val countInt = 1000L
val minBound = 1
val maxBound = 10_000

class AsyncClientH2Multiplexing {

    @Throws(Exception::class)
//    @JvmStatic
    fun oneByOne(clients: HttpAsyncClient, allowEtag: Boolean) {

//        val leaseFuture: Future<AsyncClientEndpoint> = client.lease(target, null)
//        val endpoint: AsyncClientEndpoint = leaseFuture[30, TimeUnit.SECONDS]

        try {
            val ids = ThreadLocalRandom.current().ints(countInt, minBound, maxBound).toList()
            val requestUris =  ids.map { "/rest/owners/$it" }

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

//                println("Executing request $request")
                client.execute(
                    SimpleRequestProducer.create(request),
                    SimpleResponseConsumer.create(),
                    object : FutureCallback<SimpleHttpResponse?> {
                        override fun completed(response : SimpleHttpResponse?) {
                            latch.countDown()
//                            println(request.toString() + "->" + StatusLine(response))
//                            println("Request Headers" + request.headers.toList())
//                            println("Response Headers" + response?.headers?.toList())
//                            println(response?.getBody())

                            if (StatusLine(response).statusCode == 200) {
                                etags.putIfAbsent(requestUri, response?.getFirstHeader(HttpHeaders.ETAG)?.value ?: "")
                            }
                        }

                        override fun failed(ex: Exception) {
                            latch.countDown()
                            println(request.toString() + "->" + ex)
                        }

                        override fun cancelled() {
                            latch.countDown()
                            println(request.toString() + " cancelled")
                        }
                    })

            }
            latch.await()
        } finally {
//            endpoint.releaseAndReuse()
        }
    }

    @Throws(Exception::class)
//    @JvmStatic
    fun getMany(clients: HttpAsyncClient) {

//        val leaseFuture: Future<AsyncClientEndpoint> = client.lease(target, null)
//        val endpoint: AsyncClientEndpoint = leaseFuture[30, TimeUnit.SECONDS]

        try {

            val ids = ThreadLocalRandom.current().ints(countInt, minBound, maxBound).toList()
            val requestUris =  arrayOf("/rest/owners/by-ids?ids=${ids.joinToString(",")}")

            val latch = CountDownLatch(requestUris.size)
            for (requestUri in requestUris) {
                val requestBuilder = SimpleRequestBuilder.get()
                    .setHttpHost(target)
                    .setPath(requestUri)
                    .addHeader(HttpHeaders.ACCEPT, "application/json")
                    .addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip")

                val request = requestBuilder.build()

//                println("Executing request $request")
                client.execute(
                    SimpleRequestProducer.create(request),
                    SimpleResponseConsumer.create(),
                    object : FutureCallback<SimpleHttpResponse?> {
                        override fun completed(response : SimpleHttpResponse?) {
                            latch.countDown()
//                            println(request.toString() + "->" + StatusLine(response))
//                            println("Request Headers" + request.headers.toList())
//                            println("Response Headers" + response?.headers?.toList())
//                            println(response?.getBody())
                        }

                        override fun failed(ex: Exception) {
                            latch.countDown()
                            println(request.toString() + "->" + ex)
                        }

                        override fun cancelled() {
                            latch.countDown()
                            println(request.toString() + " cancelled")
                        }
                    })

            }
            latch.await()
        } finally {
//            client.releaseAndReuse()
        }
    }
}

//kotlinc -script hurl/http2.main.kts
// Create an Ehcache-based cache storage
//val cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
//    .withCache("httpCache",
//        CacheConfigurationBuilder.newCacheConfigurationBuilder(
//            String::class.java, String::class.java,
//            ResourcePoolsBuilder.newResourcePoolsBuilder()
//                .heap(100, EntryUnit.ENTRIES)
//                .offheap(10, MemoryUnit.MB)
//        )
//    ).build(true)
//
//val cache = cacheManager.getCache("httpCache", String::class.java, String::class.java)
//val cacheStorage: HttpCacheStorage = EhcacheHttpCacheStorage(cache)

val cacheConfig = CacheConfig
    .custom()
    .setMaxObjectSize(500000) // 500KB
    .setMaxCacheEntries(200000)
    // Set this to false and a response with queryString
    // will be cached when it is explicitly cacheable .setNeverCacheHTTP10ResponsesWithQueryString(false)
    .build();

val client = CachingH2AsyncClientBuilder.create()
    .setCacheConfig(cacheConfig).build()

//val client = CachingHttpAsyncClientBuilder.create()
//    .setCacheConfig(cacheConfig).build()

//    .setConnectionManager(PoolingAsyncClientConnectionManagerBuilder.create()
////        .setMaxConnTotal(100)
////        .setMaxConnPerRoute(100)
//        .setDefaultTlsConfig(
//            TlsConfig.custom()
//                .setVersionPolicy(HttpVersionPolicy.FORCE_HTTP_2)
//                .build()
//        ))
//val client = CachingHttpAsyncClients
//    .customHttp2().setCacheConfig(cacheConfig).build()

//    .setCacheConfig(HttpCacheContext.DEFAULT)
//    .setHttpCacheStorage(cacheStorage)
//    .setConnectionManager(PoolingAsyncClientConnectionManagerBuilder.create()
////        .setMaxConnTotal(100)
////        .setMaxConnPerRoute(100)
//        .setDefaultTlsConfig(
//            TlsConfig.custom()
//                .setVersionPolicy(HttpVersionPolicy.FORCE_HTTP_2)
//                .build()
//        ))
//    .build()


//val client: MinimalHttpAsyncClient = HttpAsyncClients.createMinimal(
//            H2Config.DEFAULT,
//            Http1Config.DEFAULT,
//            IOReactorConfig.DEFAULT,
//    PoolingAsyncClientConnectionManagerBuilder.create()
////        .setMaxConnTotal(100)
////        .setMaxConnPerRoute(100)
//        .setDefaultTlsConfig(
//            TlsConfig.custom()
//                .setVersionPolicy(HttpVersionPolicy.FORCE_HTTP_2)
//                .build()
//        )
//        .build()
//)

client.start()


val measureTime = measureTime {
    AsyncClientH2Multiplexing().oneByOne(client, false)
}

println("First time (Gzip): $measureTime")

(0..1000).forEach {
//    val secondTime = measureTime {
//        AsyncClientH2Multiplexing().oneByOne(client, true)
//    }

//    println("Second time (Gzip): " + measureTime {
//        AsyncClientH2Multiplexing().oneByOne(client, false)
//    })

    println("Second time (Gzip): " + measureTime {
        AsyncClientH2Multiplexing().oneByOne(client, false)
    })

    println("Second time (Gzip + ETag): " + measureTime {
        AsyncClientH2Multiplexing().oneByOne(client, true)
    })

//    if (it % 8 == 0) {
        println("ByIds (ETag): " + measureTime {
            AsyncClientH2Multiplexing().getMany(client)
        })
//    }


    println("Waiting for 10 seconds...")

    Thread.sleep(10)
}


client.close(CloseMode.GRACEFUL)
