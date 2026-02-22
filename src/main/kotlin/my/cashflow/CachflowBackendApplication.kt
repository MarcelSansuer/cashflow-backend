package my.cashflow

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CachflowBackendApplication

fun main(args: Array<String>) {
    runApplication<CachflowBackendApplication>(*args)
}
