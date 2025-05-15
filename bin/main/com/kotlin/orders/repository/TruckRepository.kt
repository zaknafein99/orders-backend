package com.kotlin.orders.repository

import com.kotlin.orders.entity.Truck
import org.springframework.data.repository.CrudRepository

interface TruckRepository : CrudRepository<Truck, Int> {

}
