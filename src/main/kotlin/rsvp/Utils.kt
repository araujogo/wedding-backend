package rsvp

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and

fun HikariConfig.ds() : HikariDataSource {
    jdbcUrl = "jdbc:mysql://localhost/wedding"
    username = "root"
    password = "admin"
    driverClassName = "com.mysql.jdbc.Driver"

    return HikariDataSource(this)
}

operator fun Op<Boolean>.plus(element: Op<Boolean>): Op<Boolean> {
    return this and element
}

fun String.separa(): List<String>{
    return trim().replace("\\s+".toRegex(), " ").split(" ")
}