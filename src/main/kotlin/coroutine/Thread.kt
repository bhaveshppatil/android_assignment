package coroutine

import kotlin.concurrent.thread

fun main() {

    println("Main program started: ${Thread.currentThread().name}")

    thread {
        //background thread (worker thread)
        println("Work started: ${Thread.currentThread().name}")
        Thread.sleep(1000)      // doing some work like uploading file, api call
        println("Work finished: ${Thread.currentThread().name}")
    }

    println("Main program ends: ${Thread.currentThread().name}")
}