package notes.multi.utilities

import notes.multi.utilities.Note

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.io.path.createTempDirectory

class DatabaseOperations() {
    object Notes : IntIdTable() {
        val title: Column<String?> = varchar("title", 100).nullable()
        val text = text("text", eagerLoading = true).nullable()
        val dateCreated = varchar("dateCreated", 30)
        val lastModified = varchar("lastModified", 30)
    }

    companion object CRUD {
        fun addNote(note: Note) {
            val newNote = Notes.insert {
                it[Notes.title] = note.title
                it[Notes.text] = note.text.toString()
                it[Notes.dateCreated] = note.dateCreated
                it[Notes.lastModified] = note.lastModified
            } get Notes.id
        }

        fun getNote(id: Int): Note {
            val tempNote = Note()

            Notes.select { Notes.id eq id }.forEach {
                tempNote.title = it[Notes.title]
                tempNote.text = StringBuffer(it[Notes.text])
                tempNote.dateCreated = it[Notes.dateCreated]
                tempNote.lastModified = it[Notes.lastModified]
            }

            return tempNote
        }

        fun updateNote(id: Int, note: Note) {
            Notes.update ({Notes.id eq id}) {
                it[Notes.title] = note.title
                it[Notes.text] = note.text.toString()
                it[Notes.dateCreated] = note.dateCreated
                it[Notes.lastModified] = note.lastModified
            }
        }

        fun deleteNote(id: Int) {
            Notes.deleteWhere { Notes.id eq id }
            updateNoteId()
        }

        private fun updateNoteId() {
            var id = 1
            Notes.slice(Notes.id).selectAll().forEach() {
                Notes.update ({Notes.id eq it[Notes.id]}) {
                    it[Notes.id] = id
                }
                id += 1
            }
        }

        fun getAllNotes(): MutableList<Note> {
            Database.connect("jdbc:sqlite:test.db")
            var listOfNotes: MutableList<Note> = mutableListOf()
            transaction {
                Notes.selectAll().forEach {
                    var tempNote = Note(
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
    }


//object Folder : IntIdTable() {
//    val folderName = varchar("folderName", 100).nullable()
//    val notes = arrayOf(Notes.id)
//    val dateCreated = varchar("dateCreated", 30)
//    val lastUpdated = varchar("lastUpdated", 30)
//}
}