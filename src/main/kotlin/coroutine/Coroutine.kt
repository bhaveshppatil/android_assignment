package coroutine

import kotlinx.coroutines.*
fun main() {

    println("Main program starts: ${Thread.currentThread().name}")

    GlobalScope.launch {
        //background coroutine that runs on a background thread
        println("work started: ${Thread.currentThread().name}")
        Thread.sleep(1000)      //doing some work
        println("Work finished: ${Thread.currentThread().name}")
    }

    // Blocks the current main thread & wait for coroutine to finish (practically not a right way to wait)
    Thread.sleep(2000)
    println("Main program ends: ${Thread.currentThread().name}")
}