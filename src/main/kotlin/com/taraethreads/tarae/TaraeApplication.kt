package com.taraethreads.tarae

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class TaraeApplication

fun main(args: Array<String>) {
    runApplication<TaraeApplication>(*args)
}
