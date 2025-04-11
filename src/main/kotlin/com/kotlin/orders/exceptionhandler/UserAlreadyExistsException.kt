package com.kotlin.orders.exceptionhandler

class UserAlreadyExistsException(email: String) : RuntimeException("User with email $email already exists")