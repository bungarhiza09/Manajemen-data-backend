package org.delcom.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {

        val db = Database.connect(
            url = "jdbc:postgresql://localhost:5432/smkn3_balige",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "passwordmu"
        )

        transaction(db) {
            SchemaUtils.create(GuruTable, SiswaTable)
        }

    }
}