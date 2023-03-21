package notes.multi.utilities

import com.beust.klaxon.Klaxon
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class HttpOperations {
    companion object Request {
        fun get(id: String = ""): Note {
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/notes/$id"))
                .GET()
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            val remoteNote = Klaxon().parse<RemoteNote>(response.body())
            return Note(remoteNote!!.id, remoteNote.title, StringBuffer(remoteNote.text), remoteNote.dateCreated, remoteNote.lastModified)
        }

        fun post(note: Note): String {
            val tempNote = RemoteNote(note.id, note.title, note.text.toString(), note.dateCreated, note.lastModified)

            val jsonNote = Klaxon().toJsonString(tempNote)
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/notes/"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonNote))
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            return response.body()
        }

        fun put(note: Note): String {
            val tempNote = RemoteNote(note.id, note.title, note.text.toString(), note.dateCreated, note.lastModified)

            val jsonNote = Klaxon().toJsonString(tempNote)
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/notes/${tempNote.id}"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonNote))
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            return response.body()
        }

        fun delete(id: String): String {
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/notes/$id"))
                .DELETE()
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            return response.body()
        }
    }
}