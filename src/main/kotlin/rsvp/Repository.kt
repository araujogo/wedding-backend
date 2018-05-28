package rsvp

import com.zaxxer.hikari.HikariConfig
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun buscaConvidado(nomeSobrenome: String): Map<UUID, String>{
    Database.connect(HikariConfig().ds())
    val acompanhantes = mutableMapOf<UUID, String>()

    transaction {
        (Acompanhante innerJoin Convidado).slice(Acompanhante.id, Acompanhante.nome).
                select{
                    whereNomesLike(nomeSobrenome.separa())
                }.forEach{
            acompanhantes[it[Acompanhante.id].value] = it[Acompanhante.nome]
            //TODO colocar informacoes do convidado principal e retornar para também ser confirmado na tela de cinfirmacao
        }

    }
    return acompanhantes
}

fun whereNomesLike(palavras: List<String>): Op<Boolean>{
    if(palavras.isNotEmpty()){
        val nome = palavras[0]
        val sobrenomes = palavras - nome

        val opNome = Convidado.nome like "%${palavras[0]}%"
        var opSobrenomes = Convidado.nome like "%${sobrenomes[0]}%"

        for((index, palavra) in sobrenomes.withIndex()){
            if(palavra.length <= 2 || index == 0)
                continue

            opSobrenomes = opSobrenomes.or(Convidado.nome like "%$palavra%")
        }
        return opNome and opSobrenomes
    }
    throw RuntimeException("Nao eh possivel fazer busca sem palavras")
}


fun main(args: Array<String>) {
    transaction(Database.connect(HikariConfig().ds())) {
        //        create(Convidado, Acompanhante)

        val convidadoId = Convidado.insert{
            it[nome] = "Vinicius Araujo"
            it[telefone] = "(62)3225-8471"
            it[confirmou] = false
        } get Convidado.id

        Acompanhante.insert {
            it[nome] = "Savia Miranda"
            it[confirmou] = false
            it[idConvidado] = convidadoId!!
        }
        Acompanhante.insert {
            it[nome] = "Eric Araujo"
            it[confirmou] = false
            it[idConvidado] = convidadoId!!
        }

    }
}
//    buscaConvidado("Vinicius Araujo")

