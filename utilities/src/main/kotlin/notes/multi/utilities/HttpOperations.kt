package notes.multi.utilities

import com.beust.klaxon.Klaxon
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class HttpOperations {

    companion object Request {
        /**
         * Endpoint for the web service
         * Change accordingly
         *  - Testing: Run web service in IntelliJ and point to "http://localhost:8080/"
         *  - Local Production: Locally deploy app using Tomcat and point to "http://localhost:8080/notes-app"
         */
        private const val WebServiceEndpoint = "http://18.117.170.43:8080/notes-app"
        fun get(id: String = ""): Note {
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("${WebServiceEndpoint}/notes/$id"))
                .GET()
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            val remoteNote = Klaxon().parse<RemoteNote>(response.body())
            return Note(remoteNote!!.id, remoteNote.title, StringBuffer(remoteNote.text), remoteNote.dateCreated, remoteNote.lastModified)
        }

        fun getAllNotes(): MutableList<Note> {
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("${WebServiceEndpoint}/notes/"))
                .GET()
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            val remoteNote = Klaxon().parseArray<RemoteNote>(response.body())!!.toMutableList()

            val returnNote = mutableListOf<Note>()

            for (note in remoteNote) {
                val tempNote: Note = Note(note.id, note.title, StringBuffer(note.text), note.dateCreated, note.lastModified)
                returnNote.add(tempNote)
            }

            return returnNote
        }

        fun post(note: Note): String {
            val tempNote = RemoteNote(note.id, note.title, note.text.toString(), note.dateCreated, note.lastModified)

            val jsonNote = Klaxon().toJsonString(tempNote)
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("${WebServiceEndpoint}/notes/"))
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
                .uri(URI.create("${WebServiceEndpoint}/notes/${tempNote.id}"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonNote))
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            return response.body()
        }

        fun delete(id: String): String {
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("${WebServiceEndpoint}/notes/$id"))
                .DELETE()
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            return response.body()
        }

        fun lastUpdate(): String {
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("${WebServiceEndpoint}/lastSave/"))
                .GET()
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            val lastSave = Klaxon().parse<LastSave>(response.body())
            return lastSave!!.lastUpdate
        }

        fun sync(listOfNotes: MutableList<Note>, lastUpdate: String): String {
            val listOfRemoteNotes: MutableList<RemoteNote> = mutableListOf()

            for (note in listOfNotes) {
                val tempRemoteNote = RemoteNote(note.id, note.title, note.text.toString(), note.dateCreated, note.lastModified)
                listOfRemoteNotes.add(tempRemoteNote)
            }

            val syncRequest = SyncRequest(lastUpdate, listOfRemoteNotes)

            val jsonNote = Klaxon().toJsonString(syncRequest)
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("${WebServiceEndpoint}/sync/"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonNote))
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            return response.body()
        }

        fun addUpdateNote(note: Note): Boolean {
            var locexist = false
            val localnotes = DatabaseOperations.getAllNotes()
            for (i in localnotes) {
                if (i.id == note.id) {
                    locexist = true
                    break
                }
            }

            if (locexist) {

                var exist = false

                val notesList: MutableList<Note> = getAllNotes()

                for (remoteNote in notesList) {
                    if (note.id == remoteNote.id) {
                        exist = true;
                        break;
                    }
                }

                if (exist) {
                    put(note)
                } else {
                    post(note)
                }
                return true
            } else {
                return false
            }
        }
    }
}