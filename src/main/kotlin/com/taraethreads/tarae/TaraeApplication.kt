package com.taraethreads.tarae

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TaraeApplication

fun main(args: Array<String>) {
    runApplication<TaraeApplication>(*args)
}
