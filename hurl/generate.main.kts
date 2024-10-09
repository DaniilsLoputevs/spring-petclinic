//@file:DependsOn("space.kscience:kmath-for-real:0.4.0")

import java.io.File
import java.math.BigInteger
import kotlin.math.pow
import kotlin.system.exitProcess

val range = 1..500_000
val position = 1..50

//val result = mutableSetOf<List<Int>>()

//for (i in 1..range.endInclusive.toDouble().pow(position.endInclusive).toInt()) {
//
//}
//val allResult = mutableListOf<List<Int>>()
//for (i in position) {
//    val result = range.toMutableList().map { listOf(it) }.toMutableSet()
////    println(result)
//    for (j in position) {
//        val els = result.flatMap { r -> range.map({ r + it }).map { it.sorted().distinct() } }
//        result.addAll(els)
////        println(els)
////        println(result)
//        println("$j: ${result.size}")
//    }

// write to file
//val file = File("result.txt")
//file.writeText(result.joinToString("\n") { it.joinToString(" ") })


//    allResult.addAll(result)
//}

//println(result)
//    for (j in range) {
//        result.add(listOf(i, j))
//    }
//}



fun factorial(n: Int): BigInteger {
    var result = BigInteger.ONE
    for (i in 1..n) {
        result = result.multiply(BigInteger.valueOf(i.toLong()))
    }
    return result
}


//println(range.last() - position.last())
//println(range.endInclusive)
//println(position.endInclusive)
//println(factorial(range.count() - position.count()))
var a = 0.toBigInteger()
val rangeFactorial = factorial(range.count())

for (i in position) {
    a = a + rangeFactorial / (factorial(i) * factorial(range.count() - i))
    println("${range.count()} -> $i: $a")
}
//val a = factorial(range.count()) / factorial(range.count() - position.count())
//println(a)
