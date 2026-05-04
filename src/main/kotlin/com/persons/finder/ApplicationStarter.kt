package com.persons.finder

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

@SpringBootApplication
class ApplicationStarter {

    @Bean
    fun restTemplate() = RestTemplate()

    @Bean
    fun objectMapper() = ObjectMapper().registerKotlinModule()
}

fun main(args: Array<String>) {
    runApplication<ApplicationStarter>(*args)
}
