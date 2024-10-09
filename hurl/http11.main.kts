@file:DependsOn("org.apache.httpcomponents.client5:httpclient5:5.4")

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder
import org.apache.hc.client5.http.async.methods.SimpleRequestProducer
import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer
import org.apache.hc.client5.http.config.TlsConfig
import org.apache.hc.client5.http.impl.async.HttpAsyncClients
import org.apache.hc.client5.http.impl.async.MinimalHttpAsyncClient
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder
import org.apache.hc.core5.concurrent.FutureCallback
import org.apache.hc.core5.http.HttpHeaders
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.config.Http1Config
import org.apache.hc.core5.http.message.StatusLine
import org.apache.hc.core5.http2.HttpVersionPolicy
import org.apache.hc.core5.http2.config.H2Config
import org.apache.hc.core5.io.CloseMode
import org.apache.hc.core5.reactor.IOReactorConfig
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import kotlin.streams.toList
import kotlin.time.measureTime


val etags = mutableMapOf<String, String>()
val target = HttpHost("http", "localhost", 8000)
val countInt = 3000L
val minBound = 1
val maxBound = 50000

/**
 * This example demonstrates pipelined execution of multiple HTTP/1.1 message exchanges.
 */
class AsyncClientHttp1Pipelining {
    @Throws(Exception::class)
//    @JvmStatic
    fun oneByOne(client: MinimalHttpAsyncClient, allowEtag: Boolean) {

        val leaseFuture = client.lease(target, null)
        val endpoint = leaseFuture[30, TimeUnit.SECONDS]
        try {
            val ids = ThreadLocalRandom.current().ints(countInt, minBound, maxBound).toList()
            val requestUris =  ids.map { "/rest/owners/$it" }

            val latch = CountDownLatch(requestUris.size)
            for (requestUri in requestUris) {
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
                endpoint.execute(
                    SimpleRequestProducer.create(request),
                    SimpleResponseConsumer.create(),
                    object : FutureCallback<SimpleHttpResponse?> {
                        override fun completed(response: SimpleHttpResponse?) {
                            latch.countDown()
//                            println(request.toString() + "->" + StatusLine(response))
//                            println(response?.body)

                            val statusLine = StatusLine(response)
//                            if (statusLine.statusCode != 200 && statusLine.statusCode != 304) {
//                                println(request.toString() + "->" + StatusLine(response))
//                            }
                            if (statusLine.statusCode == 200) {
                                etags.putIfAbsent(requestUri, response?.getFirstHeader(HttpHeaders.ETAG)?.value ?: "")
                            }
                        }

                        override fun failed(ex: Exception) {
                            latch.countDown()
                            println("$request->$ex")
                        }

                        override fun cancelled() {
                            latch.countDown()
                            println("$request cancelled")
                        }
                    })
            }
            latch.await()
        } finally {
            endpoint.releaseAndReuse()
        }
    }
}

val client: MinimalHttpAsyncClient = HttpAsyncClients.createMinimal(
    H2Config.DEFAULT,
    Http1Config.DEFAULT,
    IOReactorConfig.DEFAULT,
    PoolingAsyncClientConnectionManagerBuilder.create()
        .setMaxConnTotal(100)
        .setMaxConnPerRoute(100)
        .setDefaultTlsConfig(
            TlsConfig.custom()
                .setVersionPolicy(HttpVersionPolicy.FORCE_HTTP_1)
                .build()
        )
        .build()
)

client.start()


val measureTime = measureTime {
    AsyncClientHttp1Pipelining().oneByOne(client, false)
}

println("First time (Gzip): $measureTime")

(0..100_000).forEach {
//    val secondTime = measureTime {
//        AsyncClientH2Multiplexing().oneByOne(client, true)
//    }

//    println("Second time (Gzip): " + measureTime {
//        AsyncClientH2Multiplexing().oneByOne(client, false)
//    })

    println("Second time (Gzip + ETag): " + measureTime {
        AsyncClientHttp1Pipelining().oneByOne(client, true)
    })

//    if (it % 8 == 0) {
//    println("ByIds (ETag): " + measureTime {
//        AsyncClientH2Multiplexing().getMany(client)
//    })
//    }


    println("Waiting for 10 seconds...")

    Thread.sleep(10)
}


client.close(CloseMode.GRACEFUL)
