package rsvp

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get

fun Application.main(){
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation){
        gson {
            setPrettyPrinting()
        }
    }
    install(Routing){
        root()
        confirm()
    }
}

fun Routing.root(){
    get("/"){
        val params = call.parameters

        val pessoa = Pessoa(params["nome"] ?: "sem nome", params["idade"]?.toInt() ?: 0)

        //call.respondText { "Hello, o nome do felizardo eh ${pessoa.nome} e ele tem ${if (pessoa.idade == 0) "Sem idade" else pessoa.idade}" }

        call.respond(pessoa)
    }
}

fun Routing.confirm(){
    //TODO fazer confirmacao
}

data class Pessoa(val nome: String, val idade:Int)