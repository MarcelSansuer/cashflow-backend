package my.cashflow.account.api.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<Map<String, String?>> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to e.message))
}