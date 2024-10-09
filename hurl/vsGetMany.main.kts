@file:DependsOn("io.rest-assured:rest-assured:5.5.0")


import io.restassured.RestAssured.baseURI
import io.restassured.RestAssured.given
import org.apache.http.HttpHeaders
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

baseURI = "http://localhost:8000/rest"

// todo: Getting minimal
var ownerIds = emptyList<Int>()
val ownerIdsTime = measureTimeMillis {
    ownerIds = given()
        .header(HttpHeaders.ACCEPT, "application/json")
        .header(HttpHeaders.ACCEPT_ENCODING, "gzip")
//    .log().all()
    .`when`()
        .get("/owners/minimal")
    .then()
//    .log().all()
        .statusCode(200)
    .extract().response().path<List<Int>>("content.id")
}

println("Owner Ids: $ownerIds")
// todo: Getting by-ids
val getManyTime = measureTimeMillis {
    given()
        .header(HttpHeaders.ACCEPT, "application/json")
        .header(HttpHeaders.ACCEPT_ENCODING, "gzip")
//        .log().all()
        .queryParam("ids", ownerIds.joinToString(","))
    .`when`()
        .get("/owners/by-ids")
    .then()
//        .log().all()
        .statusCode(200)
}

// todo: Getting recursively
var etagMap: Map<Int, String>

var executor = Executors.newFixedThreadPool(16);

val oneByOneTime = measureTimeMillis {
    val requests = ownerIds.map { id ->
        Callable {
            val response = given()
                .header(HttpHeaders.ACCEPT, "application/json")
                .header(HttpHeaders.ACCEPT_ENCODING, "gzip")
//        .log().all()
            .`when`()
                .get("/owners/$id")
            .then()
//        .log().all()
                .statusCode(200)
            .extract().response()

            Pair(id, response.getHeader(HttpHeaders.ETAG))
        }
    }

    etagMap = executor.invokeAll(requests)
        .map { it.get() }.associate { it.first to it.second }
}



// getting with Etag
val oneByOneEtagTime = measureTimeMillis {
    val requests = ownerIds.map { id ->
        Callable {
            given()
                .header(HttpHeaders.ACCEPT, "application/json")
                .header(HttpHeaders.ACCEPT_ENCODING, "gzip")
                .header(HttpHeaders.IF_NONE_MATCH, etagMap[id])
//        .log().all()
            .`when`()
                .get("/owners/$id")
            .then()
//        .log().all()
                .statusCode(304)
        }
    }

    executor.invokeAll(requests).forEach { it.get() }
}

executor.shutdown();


println("""
    |ownerIdsTime: $ownerIdsTime ms
    |getManyTime: $getManyTime ms
    |oneByOneTime: $oneByOneTime ms
    |oneByOneEtagTime: $oneByOneEtagTime ms
    |""".trimMargin())
