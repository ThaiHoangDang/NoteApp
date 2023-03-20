package notes.multi.utilities

import com.beust.klaxon.Klaxon
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class HttpOperations {
    companion object Request {
        fun get(id: String = ""): String {
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://pokeapi.co/api/v2/pokemon/$id"))
                .GET()
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            return response.body()
        }

        fun post(note: Note): String {
            val jsonNote = Klaxon().toJsonString(note)
            println(jsonNote)
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://36eb19dc-1dd7-455d-ba32-68c2cd9d307e.mock.pstmn.io"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonNote))
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            return response.body()
        }

        fun put(note: Note): String {
            val jsonNote = Klaxon().toJsonString(note)
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://36eb19dc-1dd7-455d-ba32-68c2cd9d307e.mock.pstmn.io"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonNote))
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            return response.body()
        }

        fun delete(id: String): String {
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("http....$id"))
                .DELETE()
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            return response.body()
        }
    }
}