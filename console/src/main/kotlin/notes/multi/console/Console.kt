/**
 * Unless explicitly stated otherwise all files in this repository are licensed under the MIT License
 * Copyright (c) 2023 Abhay Menon, Inseo Kim, Hoang Dang, Guransh Khurana, Anshul Ruhil
 */
package notes.multi.console

import java.lang.IllegalArgumentException

import notes.multi.utilities.TextWindow
import javafx.application.Application
import notes.multi.utilities.DatabaseOperations
import notes.multi.utilities.DatabaseOperations.CRUD.getAllNotes
import notes.multi.utilities.Note
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction



fun main(args: Array<String>) {
    try {
        if (args.isEmpty()) {
            println(
                """
        +-------------------------------------------------------------------------------------+
        WELCOME TO TEAM 112'S CONSOLE BASED NOTES APP
         
        This application is a working console-based prototype for the
        note taking application that the team will be delivering at the end of the term.
        
        With this simple app, users can:
            1. Create Notes
            2. Edit Notes
            3. Delete Notes
            4. Close GUI Window
            
         1. Create Notes
            - To create notes, users must type "./console <filename>"
            - To save changes, use the Ctrl + S keybinding
            
         2. Edit Notes
            - To edit notes, users must type "./console <filename>"
            - If the file doesn't exist, a new note will be created
            - To save changes, use the Ctrl + S keybinding
            
         3. Delete Notes
            - To delete a note, use the Ctrl + D keybinding
            
         4. Close GUI Window
            - To close the GUI window, use the Ctrl + W keybinding
       
        +-------------------------------------------------------------------------------------+
    """.trimIndent()
            )
            Database.connect("jdbc:sqlite:test.db")

            transaction {
                // print sql to std-out
                // addLogger(StdOutSqlLogger)

                // create a table that reflects the Cities class structure
                SchemaUtils.create(DatabaseOperations.Notes)

                val query = DatabaseOperations.Notes.selectAll()
                query.forEach {
                   println(it[DatabaseOperations.Notes.id].toString() + " | " + it[DatabaseOperations.Notes.title])
                }
            }
        } else {
            if (args.size < 2) {

                // /**
                //  * File Path (can be relative or absolute)
                //  */
                // val filePathArg = args[0]
                //
                // /**
                //  * Title of the file
                //  */
                // val fileTitle = Path(filePathArg).fileName
                //
                // /**
                //  * Location of the file as text
                //  */
                // val fileLocation = Path(filePathArg).parent ?: System.getProperty("user.dir")
                //
                // if (!File(fileLocation.toString()).isDirectory) throw IllegalArgumentException("[ERROR]: Directory does not exist!")

                // Regex Check for a specific argument format:
                // ConsoleUtils.verifyFilename(fileTitle.toString(), Regex("^.*[.]([Mm][Dd]|[Tt][Xx][Tt])$"))

                val fileTitle = args[0]
                var fileText: String = ""
                    Database.connect("jdbc:sqlite:test.db")

                var noteExists = false
                var noteText: String? = ""
                var noteId: String = "-1"

                transaction {
                    // print sql to std-out
                    // addLogger(StdOutSqlLogger)

                    // create a table that reflects the Cities class structure
                    SchemaUtils.create(DatabaseOperations.Notes)


                    val query = DatabaseOperations.Notes.selectAll()
                    query.forEach {
                        if (it[DatabaseOperations.Notes.title] == fileTitle) {
                            noteExists = true
                            noteText = it[DatabaseOperations.Notes.text]
                            noteId = it[DatabaseOperations.Notes.id].toString()
                        }
                    }
                }

                // Passing the location and title as params to TextWindow
                Application.launch(TextWindow()::class.java, "--title=$fileTitle", "--text=$noteText", "--id=$noteId")
            } else {
                throw IllegalArgumentException("[ERROR]: Wrong number of arguments provided!")
            }
        }
    } catch (err: IllegalArgumentException) {
        System.err.println(err.message)
    }
}

