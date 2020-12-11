package de.scaramangado.announcer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class AnnouncerApplication

fun main(args: Array<String>) {
    runApplication<AnnouncerApplication>(*args)
}
