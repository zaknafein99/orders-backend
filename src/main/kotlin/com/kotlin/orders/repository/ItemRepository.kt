package com.kotlin.orders.repository

import com.kotlin.orders.entity.Item
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ItemRepository : JpaRepository<Item, Int> {
    override fun findById(id: Int): Optional<Item>

}
