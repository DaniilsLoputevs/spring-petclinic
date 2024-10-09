@file:DependsOn("org.apache.commons:commons-collections4:4.4")
@file:DependsOn("org.apache.commons:commons-math3:3.6.1")

import org.apache.commons.math3.distribution.NormalDistribution
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.max
import kotlin.streams.toList


fun elements(howMuch: Int, distr: NormalDistribution): Map<Int, Int> {
    val elements = distr.sample(howMuch * 10).map { it.toInt() }.filter { it in 1..50 }.sorted()
    val elementsGrouped = elements.groupBy { it }
    val reduceElem = (elements.count() - howMuch) / elementsGrouped.keys.count()
    return elements.groupBy { it }.mapValues { max(it.value.count() - reduceElem, 1) }
}

//println( normalDistribution.sample(10).map{ it.toInt() }.sorted())
fun norPrint(howMuch: Int, distr: NormalDistribution) {
    val elements = distr.sample(howMuch * 10).map { it.toInt() }.filter { it in 1..50 }.sorted()
    val elementsGrouped = elements.groupBy { it }
    val reduceElem = (elements.count() - howMuch) / elementsGrouped.keys.count()
    elements.groupBy { it }.map { ".".repeat(max(it.value.count() - reduceElem, 1)) + "-> ${it.key}" }.forEach(::println)

    println("\n ############################### \n")
}

val count = 500_000
//val count = 100_000
val minElem = 1
val maxElem = 3000
//println("\n ############################### 25.0, 40.0\n")

//var elements = elements(count, NormalDistribution(25.0, 40.0))

//elements.map { "${it.value}" + " -> ${it.key}"}.forEach(::println)

//println("\n ############################### 5.0, 50.0\n")

//val elements = elements(count, NormalDistribution(5.0, 50.0))
//println("All elements: ${elements.values.sum()}")
//elements.map { ".".repeat(it.value) + " -> ${it.key}"}.forEach(::println)
//
//println("\n ############################### 1.0, 50.0\n")
//
//val elements = elements(count, NormalDistribution(1.0, 50.0))
//println("All elements: ${elements.values.sum()}")
//elements.map { ".".repeat(it.value) + " -> ${it.key}"}.forEach(::println)
//
//println("\n ############################### 50.0, 50.0\n")
//
val elements = elements(count, NormalDistribution(50.0, 50.0))
//println("All elements: ${elements.values.sum()}")
//elements.map { ".".repeat(it.value) + " -> ${it.key}"}.forEach(::println)

//(0..20).map{ normalDistribution.sample()}.sorted().forEach(::println)

println("All elements: ${elements.values.sum()}")

elements.flatMap { e -> (1..e.value + 100).map { ThreadLocalRandom.current().ints(e.key.toLong(), minElem, maxElem).sorted() }.distinct() }
    .map { it.toList().joinToString(",") }.shuffled().forEach(::println)
