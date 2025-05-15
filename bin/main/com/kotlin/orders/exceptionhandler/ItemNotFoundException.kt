package com.kotlin.orders.exceptionhandler

import java.lang.RuntimeException

class ItemNotFoundException(message: String) : RuntimeException(message) {

}
