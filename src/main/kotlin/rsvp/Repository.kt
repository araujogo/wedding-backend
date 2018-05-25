package rsvp

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

fun testaBanco(){
    Database.connect("jdbc:mysql://localhost/folhaweb2", driver = "com.mysql.jdbc.Driver", user = "root", password = "admin")

    transaction {
        val empresa = EmpresaObj.get(1)

        println("Empresa ${empresa.nome}")
    }
}

object Empresa: IntIdTable(){
    val nome = varchar("nome", 100)

}

class EmpresaObj(id: EntityID<Int>): IntEntity(id){
    companion object : IntEntityClass<EmpresaObj>(Empresa)

    var nome by Empresa.nome
}

fun main(args: Array<String>) {
    testaBanco()
}