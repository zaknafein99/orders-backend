package com.kotlin.orders.exceptionhandler

import mu.KLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@Component
@ControllerAdvice
class GlobalErrorHandler : ResponseEntityExceptionHandler() {

  companion object : KLogging()

  override fun handleMethodArgumentNotValid(
      ex: MethodArgumentNotValidException,
      headers: HttpHeaders,
      status: HttpStatusCode,
      request: WebRequest
  ): ResponseEntity<Any> {
    logger.error("MethodArgumentNotValidException observed : ${ex.message}", ex)
    val errors = ex.bindingResult.allErrors.map { error -> error.defaultMessage!! }.sorted()

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.joinToString(", ") { it })
  }

  fun handleAllExceptions(ex: Exception, request: WebRequest): ResponseEntity<Any> {

    logger.error("Exception observed : ${ex.message}, in request ${request.toString()}", ex)

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.message)
  }

  @ExceptionHandler(UserAlreadyExistsException::class)
  fun handleUserAlreadyExistsException(ex: UserAlreadyExistsException): ResponseEntity<String> {
    // Here you can log the exception message without the stack trace
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.message)
  }

  @ExceptionHandler(CustomerAlreadyExistsException::class)
  fun handleCustomerAlreadyExistsException(
      ex: CustomerAlreadyExistsException
  ): ResponseEntity<String> {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.message)
  }
}
