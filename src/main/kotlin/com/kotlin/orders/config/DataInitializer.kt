package com.kotlin.orders.config

import com.kotlin.orders.repository.OrderRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DataInitializer(
    private val jdbcTemplate: JdbcTemplate
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(DataInitializer::class.java)

    @Transactional
    override fun run(vararg args: String?) {
        logger.info("Initializing data...")
        
        try {
            // Update existing orders with null flete to have a default value of 0.0
            val rowsUpdated = jdbcTemplate.update(
                "UPDATE orders SET flete = 0.0 WHERE flete IS NULL"
            )
            
            logger.info("Updated $rowsUpdated records with default flete values")
        } catch (e: Exception) {
            logger.error("Error updating flete values", e)
        }
        
        logger.info("Data initialization completed")
    }
} 