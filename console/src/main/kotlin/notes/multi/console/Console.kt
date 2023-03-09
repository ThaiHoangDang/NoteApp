/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package notes.multi.console

import notes.multi.utilities.Note

import java.lang.IllegalArgumentException

import notes.multi.utilities.TextWindow
import javafx.application.Application
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.io.path.Path
import java.io.File

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.time.LocalDate
import java.time.LocalDateTime

var conn: Connection? = null
fun connect() {
    try {
        val url = "jdbc:sqlite:chinook.db"
        conn = DriverManager.getConnection(url)
        println("Connection established.")
    } catch (e: SQLException) {
        println(e.message)
    }
}

fun query() {
    try {
        if (conn != null) {
            // do something
            println("Database successfully connected!")
        }
    } catch (err: Exception) {
        System.err.println("Something's wrong!")
    }
}


// fun main(args: Array<String>) {
//     {
//     // try {
//     //     if (args.isEmpty()) {
//     //         println(
//     //             """
//     //     +-------------------------------------------------------------------------------------+
//     //     WELCOME TO TEAM 112'S CONSOLE BASED NOTES APP
//     //
//     //     This application is a working console-based prototype for the
//     //     note taking application that the team will be delivering at the end of the term.
//     //
//     //     With this simple app, users can:
//     //         1. Create Notes
//     //         2. Edit Notes
//     //         3. Delete Notes
//     //         4. Close GUI Window
//     //
//     //      1. Create Notes
//     //         - To create notes, users must type "./console <filename>"
//     //         - To save changes, use the Ctrl + S keybinding
//     //
//     //      2. Edit Notes
//     //         - To edit notes, users must type "./console <filename>"
//     //         - If the file doesn't exist, a new note will be created
//     //         - To save changes, use the Ctrl + S keybinding
//     //
//     //      3. Delete Notes
//     //         - To delete a note, use the Ctrl + D keybinding
//     //
//     //      4. Close GUI Window
//     //         - To close the GUI window, use the Ctrl + W keybinding
//     //
//     //     +-------------------------------------------------------------------------------------+
//     // """.trimIndent()
//     //         )
//     //     } else {
//     //         if (args.size < 2) {
//     //
//     //             /**
//     //              * File Path (can be relative or absolute)
//     //              */
//     //             val filePathArg = args[0]
//     //
//     //             /**
//     //              * Title of the file
//     //              */
//     //             val fileTitle = Path(filePathArg).fileName
//     //
//     //             /**
//     //              * Location of the file as text
//     //              */
//     //             val fileLocation = Path(filePathArg).parent ?: System.getProperty("user.dir")
//     //             if (!File(fileLocation.toString()).isDirectory) throw IllegalArgumentException("[ERROR]: Directory does not exist!")
//     //
//     //             // Regex Check for a specific argument format:
//     //             ConsoleUtils.verifyFilename(fileTitle.toString(), Regex("^.*[.]([Mm][Dd]|[Tt][Xx][Tt])$"))
//     //
//     //             // Passing the location and title as params to TextWindow
//     //             Application.launch(TextWindow()::class.java, "--title=${fileTitle}", "--location=${fileLocation}")
//     //
//     //         } else {
//     //             throw IllegalArgumentException("[ERROR]: Wrong number of arguments provided!")
//     //         }
//     //     }
//     // } catch (err: IllegalArgumentException) {
//     //     System.err.println(err.message)
//     // }
//     }
//     connect()
//     query()
// }

object Notes : IntIdTable() {
    val title: Column<String?> = varchar("title", 100).nullable()
    val text = text("text", eagerLoading = true).nullable()
    val dateCreated = varchar("dateCreated", 30)
    val lastModified = varchar("lastModified", 30)
}

//object Folder : IntIdTable() {
//    val folderName = varchar("folderName", 100).nullable()
//    val notes = arrayOf(Notes.id)
//    val dateCreated = varchar("dateCreated", 30)
//    val lastUpdated = varchar("lastUpdated", 30)
//}

fun main(args: Array<String>) {
    //an example connection to H2 DB
    Database.connect("jdbc:sqlite:test.db")

    transaction {
        // print sql to std-out
        addLogger(StdOutSqlLogger)

        // create a table that reflects the Cities class structure
        SchemaUtils.create(Notes)

        // test an instance
        var newNote = Note("Test!", StringBuffer("Hi my name is Hoang"), LocalDate.now().toString(), LocalDateTime.now().toString())

        // test addNote
        addNote(newNote)

        // reassign instance to a new note with corresponding id
        newNote = getNote(1)
        println(newNote.lastModified)

        // update note with corresponding id
        updateNote(3, newNote)

        // delete note with corresponding id and update id
        deleteNote(9)
    }
}

// insert new note
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

fun updateNoteId() {
    var id = 1
    Notes.slice(Notes.id).selectAll().forEach() {
        Notes.update ({Notes.id eq it[Notes.id]}) {
            it[Notes.id] = id
        }
        id += 1
    }
}