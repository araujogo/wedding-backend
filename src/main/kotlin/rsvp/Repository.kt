package rsvp

import com.zaxxer.hikari.HikariConfig
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun buscaConvidado(nomeSobrenome: String): List<String>{
    Database.connect(HikariConfig().ds())
    val acompanhantes = mutableListOf<String>()

    transaction {
        (Acompanhante innerJoin Convidado).slice(Acompanhante.nome).
                select{
                    whereNomesLike(nomeSobrenome.separa())
                }.forEach{
            acompanhantes.add(it[Acompanhante.nome])
        }
    }
    return acompanhantes
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


fun main(args: Array<String>) {
    buscaConvidado("Vinicius Araujo")
}