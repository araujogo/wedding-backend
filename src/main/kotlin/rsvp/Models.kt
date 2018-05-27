package rsvp

import org.jetbrains.exposed.dao.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table


object Convidado: Table(){
    val id = uuid("id").primaryKey()
    val nome = varchar("nome", 150)
    val telefone = varchar("telefone", 14)
    val confirmou = bool("confirmou")
}

object Acompanhante: UUIDTable("acompanhante"){
    val nome = varchar("nome", 150)
    val confirmou = bool("confirmou")
    val idConvidado = (uuid("id_convidado").references(Convidado.id, ReferenceOption.CASCADE))
}