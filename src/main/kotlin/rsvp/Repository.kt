package rsvp

import com.zaxxer.hikari.HikariConfig
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun buscaConvidado(nomeSobrenome: String): List<ConvidadoGenerico>{
    Database.connect(HikariConfig().ds())
    val acompanhantes = mutableListOf<ConvidadoGenerico>()
    var convidado : ConvidadoGenerico? = null

    transaction {
        Convidado.select {
            whereNomesLike(nomeSobrenome.separa())
        }.limit(1).forEach{
            convidado = ConvidadoGenerico(it[Convidado.id].value, it[Convidado.nome], true)
        }

        acompanhantes += convidado?: throw PessoaNaoEncontradaException()

        Acompanhante.slice(Acompanhante.id, Acompanhante.nome).
                select{
                    Acompanhante.idConvidado eq convidado!!.id
                }.forEach{
            acompanhantes += ConvidadoGenerico(it[Acompanhante.id].value, it[Acompanhante.nome], false)
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

fun alteraStatusConvidados(convidado: String?, acompanhantes: List<String>){
    transaction(Database.connect(HikariConfig().ds())) {
        if (convidado != null){
            Convidado.update({Convidado.id eq UUID.fromString(convidado)}){
                it[confirmou] = true
            }
        }
        if(acompanhantes.isNotEmpty()){
            Acompanhante.update({Acompanhante.id inList acompanhantes.map { UUID.fromString(it) } }){
                it[confirmou] = true
            }
        }
    }
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

