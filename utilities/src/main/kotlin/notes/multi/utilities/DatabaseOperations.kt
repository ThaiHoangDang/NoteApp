package notes.multi.utilities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.ConnectException
import java.time.LocalDateTime
import java.time.ZoneId

class DatabaseOperations() {
    object Notes : Table() {
        val id = text("id", eagerLoading = true)
        val title = varchar("title", 100).nullable()
        val text = text("text", eagerLoading = true).nullable()
        val dateCreated = varchar("dateCreated", 30)
        val lastModified = varchar("lastModified", 30)
        override val primaryKey = PrimaryKey(id)
    }

    object LastUpdated : Table() {
        val id = text("id", eagerLoading = true)
        val lastUpdate = varchar("lastUpdated", 30)
        override val primaryKey = PrimaryKey(LastUpdated.id)
    }

    companion object CRUD {
        val zone: ZoneId = ZoneId.of("GMT")
        fun addNote(note: Note) {
            transaction {
                SchemaUtils.create(DatabaseOperations.Notes)

                val newNote = Notes.insert {
                    it[Notes.id] = note.id
                    it[Notes.title] = note.title
                    it[Notes.text] = note.text.toString()
                    it[Notes.dateCreated] = note.dateCreated
                    it[Notes.lastModified] = note.lastModified
                } get Notes.id
            }
            updateLastUpdate()
//            try {
//                HttpOperations.post(note)
//            } catch (e: ConnectException) {
//                println(e.message)
//            }
        }

        fun getNote(id: String): Note {
            var note = Note()

            transaction {
                SchemaUtils.create(DatabaseOperations.Notes)
                Notes.select { Notes.id eq id }.forEach {
                    var tempNote = Note(
                    it[Notes.id],
                    it[Notes.title],
                    StringBuffer(it[Notes.text]),
                    it[Notes.dateCreated],
                    it[Notes.lastModified])
                    note = tempNote
                }
            }
            return note
//            return HttpOperations.get(id)
        }

        fun updateNote(note: Note) {
            transaction {
                SchemaUtils.create(DatabaseOperations.Notes)

                Notes.update ({Notes.id eq note.id}) {
                    it[Notes.title] = note.title
                    it[Notes.text] = note.text.toString()
                    it[Notes.dateCreated] = note.dateCreated
                    it[Notes.lastModified] = note.lastModified
                }
            }
            updateLastUpdate()
//            HttpOperations.put(note)
        }

        fun addUpdateNote(note: Note) {
            var exist = false

            transaction {
                SchemaUtils.create(DatabaseOperations.Notes)

                Notes.selectAll().forEach {
                    if (it[Notes.id] == note.id) {
                        exist = true
                    }
                }
            }

            if (exist) {
                updateNote(note)
            } else {
                addNote(note)
            }
        }

        fun deleteNote(note: Note) {
            transaction {
                SchemaUtils.create(DatabaseOperations.Notes)

                Notes.deleteWhere { Notes.id eq note.id }
            }
            updateLastUpdate()
//            HttpOperations.delete(note.id)
        }

        fun getAllNotes(): MutableList<Note> {
            var listOfNotes: MutableList<Note> = mutableListOf()

            transaction {
                SchemaUtils.create(DatabaseOperations.Notes)

                Notes.selectAll().forEach {
                    var tempNote = Note(
                        it[Notes.id],
                        it[Notes.title],
                        StringBuffer(it[Notes.text]),
                        it[Notes.dateCreated],
                        it[Notes.lastModified]
                    )
                    listOfNotes.add(tempNote)
                }
            }
            return listOfNotes
        }

        fun deleteAllNotes() {
            transaction {
                SchemaUtils.drop(Notes)
            }
        }

        fun deleteLastUpdated() {
            transaction {
                SchemaUtils.drop(LastUpdated)
            }
        }

        fun deleteAll() {
            deleteAllNotes()
            deleteLastUpdated()
        }

        fun getLastUpdate(): String {
            var lastUpdate: String = ""

            transaction {
                SchemaUtils.create(DatabaseOperations.LastUpdated)
                LastUpdated.select { LastUpdated.id eq "1" }.forEach {
                    lastUpdate = it[LastUpdated.lastUpdate]
                }
            }
            return lastUpdate
        }

        fun updateLastUpdate(time: String = LocalDateTime.now(zone).toString()) {
            transaction {
                deleteLastUpdated()
                SchemaUtils.create(DatabaseOperations.LastUpdated)
                LastUpdated.insert {
                    it[LastUpdated.id] = "1"
                    it[LastUpdated.lastUpdate] = time
                }
            }
        }

        fun sync(): Boolean {
            try {
                val remoteLastUpdate = HttpOperations.lastUpdate()
                val localLastUpdate = getLastUpdate()

                if (localLastUpdate > remoteLastUpdate) {
                    HttpOperations.sync(DatabaseOperations.getAllNotes(), getLastUpdate())
                } else if (localLastUpdate < remoteLastUpdate) {
                    deleteAllNotes()
                    for (note in HttpOperations.getAllNotes()) {
                        addNote(note)
                    }
                    updateLastUpdate(HttpOperations.lastUpdate())
                }
                return true
            } catch (e: ConnectException) {
                println(e.message)
                return false
            }
        }

        fun remotefetch(note: Note) {
            addUpdateNote(note)
        }

        fun localfetch(note: Note): Boolean {
            val ret = HttpOperations.get(note.id)
            if (ret.id != "NOT_FOUND") {
                addUpdateNote(ret)
                return true
            }
            return false
        }

        // Delete from remote
// You can just pass note ID
// String return value is not useful, don't worry about it
        fun deleteRemote(noteId: String) {
            HttpOperations.delete(noteId)
        }

        // Delete from local
        fun deleteLocal(note: Note) {
            DatabaseOperations.deleteNote(note)
        }

        // Get all notes from remote
        fun getAllServerNotes(): MutableList<Note> {
            return HttpOperations.getAllNotes()
        }

    }
}