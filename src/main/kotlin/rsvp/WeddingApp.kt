package rsvp

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post

fun Application.main(){
    install(DefaultHeaders){
        header("Access-Control-Allow-Origin", "*")
    }
    install(CallLogging)
//    install(CORS){
//        host("localhost")
//    }
    install(ContentNegotiation){
        gson {
            setPrettyPrinting()
        }
    }
    install(Routing){
        root()
    }
}

fun Routing.root(){
    get("/"){

        val params = call.parameters
        val nome = params["nome"]
        if(nome == null) {
            call.respond(HttpStatusCode.BadRequest)
            throw RuntimeException()
        }
        try {
            val convidados = buscaConvidado(nomeSobrenome = nome)
            if(convidados.size == 1)
                call.respond(HttpStatusCode.PermanentRedirect, convidados)
            else
                call.respond(HttpStatusCode.OK, convidados)
        }catch (e: PessoaNaoEncontradaException){
            call.respond(HttpStatusCode.NotFound)
        }catch (e: RuntimeException){
            call.respond(HttpStatusCode.InternalServerError)
            e.printStackTrace()
        }
    }
    post("/"){
        val acompanahntes = call.receiveParameters().getAll("acp")
        alteraStatusConvidados(call.receiveParameters()["cnv"], acompanahntes?: emptyList())

        call.respond(HttpStatusCode.OK)
    }
}
