package notes.multi.utilities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseOperations() {
    object Notes : Table() {
        val id = text("id", eagerLoading = true)
        val title = varchar("title", 100).nullable()
        val text = text("text", eagerLoading = true).nullable()
        val dateCreated = varchar("dateCreated", 30)
        val lastModified = varchar("lastModified", 30)
        override val primaryKey = PrimaryKey(id)
    }

    companion object CRUD {
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
        }

        fun getNote(id: String): Note {
            val tempNote = Note()

            transaction {
                Notes.select { Notes.id eq id }.forEach {
                    tempNote.id = it[Notes.id]
                    tempNote.title = it[Notes.title]
                    tempNote.text = StringBuffer(it[Notes.text])
                    tempNote.dateCreated = it[Notes.dateCreated]
                    tempNote.lastModified = it[Notes.lastModified]
                }
            }
            return tempNote
        }

        fun updateNote(note: Note) {
            transaction {
                Notes.update ({Notes.id eq note.id}) {
                    it[Notes.title] = note.title
                    it[Notes.text] = note.text.toString()
                    it[Notes.dateCreated] = note.dateCreated
                    it[Notes.lastModified] = note.lastModified
                }
            }
        }

        fun deleteNote(note: Note) {
            transaction {
                Notes.deleteWhere { Notes.id eq note.id }
            }
        }

        fun getAllNotes(): MutableList<Note> {
            var listOfNotes: MutableList<Note> = mutableListOf()

            transaction {
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
    }


//object Folder : IntIdTable() {
//    val folderName = varchar("folderName", 100).nullable()
//    val notes = arrayOf(Notes.id)
//    val dateCreated = varchar("dateCreated", 30)
//    val lastUpdated = varchar("lastUpdated", 30)
//}
}