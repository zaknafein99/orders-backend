package com.kotlin.orders.util

import org.springframework.boot.CommandLineRunner
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.context.annotation.Profile

/**
 * Command line runner that resets the database by dropping and recreating critical tables.
 * Only runs when the 'db-reset' profile is active.
 */
@Component
@Profile("db-reset")
class DatabaseResetRunner(private val jdbcTemplate: JdbcTemplate) : CommandLineRunner {

    override fun run(vararg args: String) {
        println("==== DATABASE RESET UTILITY ====")
        println("Dropping order_items table...")
        jdbcTemplate.execute("DROP TABLE IF EXISTS order_items CASCADE")
        
        println("Dropping orders table...")
        jdbcTemplate.execute("DROP TABLE IF EXISTS orders CASCADE")
        
        println("Tables dropped successfully. Hibernate will recreate them on application startup.")
        println("==== DATABASE RESET COMPLETE ====")
    }
} 