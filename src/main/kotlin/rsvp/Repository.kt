package rsvp

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.dao.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

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

fun salvaPessoa(nomeI : String) : UUID{
    Database.connect(HikariConfig().ds())

    var idSalvo: UUID? = null



    return idSalvo as UUID
}

fun buscaConvidado(nomeSobrenome: List<String>){
    Database.connect(HikariConfig().ds())

    transaction {
        (Acompanhante innerJoin Convidado).slice(Convidado.nome, Acompanhante.nome).
                select{
                    whereNomesLike(nomeSobrenome)
                }.forEach{
            println("${it[Acompanhante.nome]} corresponde à: ${it[Convidado.nome]}")
        }
    }
}

fun whereNomesLike(palavras: List<String>): Op<Boolean>{
    if(palavras.isNotEmpty()){
        val nome = palavras[0]
        val sobrenomes = palavras - nome

        val opNome = Convidado.nome like "%${palavras[0]}%"
        var opSobrenomes = Convidado.nome.like("%${sobrenomes[0]}%")

        for((index, palavra) in sobrenomes.withIndex()){
            if(palavra.length <= 2 || index == 0)
                continue

            opSobrenomes = opSobrenomes.or(Convidado.nome.like("%$palavra%"))
        }
        return opNome and opSobrenomes
    }
    throw RuntimeException("Nao eh possivel fazer busca sem palavras")
}

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

fun main(args: Array<String>) {
//    buscaConvidado("Vinicius")


    buscaConvidado("Vinicius de Souza Araujo".trim().replace("\\s+".toRegex(), " ").split(" "))

}