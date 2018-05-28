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
import io.ktor.response.respondText
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
            val resultado = buscaConvidado(nomeSobrenome = nome)
            if (resultado.isEmpty())
                call.respond(HttpStatusCode.NotFound)
            else
                call.respond(HttpStatusCode.OK, resultado)

        }catch (e: RuntimeException){
            call.respond(HttpStatusCode.InternalServerError)
            e.printStackTrace()
        }
    }
    post("/"){
        val params = call.receiveParameters().getAll("acp")


        call.respondText { "OK" }

    }
}
