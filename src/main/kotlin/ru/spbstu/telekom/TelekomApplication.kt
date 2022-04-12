package ru.spbstu.telekom

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TelekomApplication

fun main(args: Array<String>) {
	runApplication<TelekomApplication>(*args)
}
