package rsvp

import org.jetbrains.exposed.dao.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption


object Convidado: UUIDTable(){
    val nome = varchar("nome", 150)
    val telefone = varchar("telefone", 14)
    val confirmou = bool("confirmou")
}

object Acompanhante: UUIDTable("acompanhante"){
    val nome = varchar("nome", 150)
    val confirmou = bool("confirmou")
    val idConvidado = reference(name = "id_convidado", foreign = Convidado, onDelete = ReferenceOption.CASCADE)
}