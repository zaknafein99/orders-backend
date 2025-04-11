package com.kotlin.orders.exceptionhandler

class CustomerAlreadyExistsException(phone: String) : RuntimeException("Customer with phone $phone already exists")