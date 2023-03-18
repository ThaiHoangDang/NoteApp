/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package notes.multi.app


import notes.multi.utilities.DatabaseOperations
import notes.multi.utilities.Note
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalDateTime
import javax.xml.crypto.Data


fun main() {
    Database.connect("jdbc:sqlite:test.db")

    // test creating Note instances
    var note1 = Note()
    var note2 = Note()

    // test UUID feature
    println("this is id of note 1: " + note1.id)
    println("this is id of note 2: " + note2.id)

    // test addNote
    DatabaseOperations.addNote(note1)
    DatabaseOperations.addNote(note2)

    // test getNote
    var note3 = DatabaseOperations.getNote(note1.id)
    println("this is id of note 3, it should be the same as note 1: " + note3.id)

    // test updateNote
    note1.title = "New Title"
    DatabaseOperations.updateNote(note1)
    println("the new title for note 1 is: " + DatabaseOperations.getNote(note1.id).title)

    // test deleteNote
    DatabaseOperations.deleteNote(note2)

    // test getAllNotes
    var notes = DatabaseOperations.getAllNotes()
    for (note in notes) {
        println(note.id + " | " + note.title)
    }

    // test addUpdateNote
    note1.text = StringBuffer("New text")
    DatabaseOperations.addUpdateNote(note1)

    var note4 = Note(title = "note4")
    DatabaseOperations.addUpdateNote(note4)


//    Application.launch(TextWindow()::class.java)
}
