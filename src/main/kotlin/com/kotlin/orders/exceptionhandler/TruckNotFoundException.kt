package com.kotlin.orders.exceptionhandler

import java.lang.RuntimeException

class TruckNotFoundException(message: String) : RuntimeException(message)
