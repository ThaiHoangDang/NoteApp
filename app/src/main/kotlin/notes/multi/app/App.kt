/**
 * Unless explicitly stated otherwise all files in this repository are licensed under the MIT License
 * Copyright (c) 2023 Abhay Menon, Inseo Kim, Hoang Dang, Guransh Khurana, Anshul Ruhil
 */
package notes.multi.app


import javafx.application.Application
import notes.multi.utilities.DatabaseOperations
import notes.multi.utilities.Note
import org.jetbrains.exposed.sql.Database
import notes.multi.utilities.TextWindow
import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    Database.connect("jdbc:sqlite:test.db")

    try {
        if (args.isEmpty()) {
            Application.launch(TextWindow()::class.java)
        } else if (args.size == 1) {
            val notesList: MutableList<Note> = DatabaseOperations.getAllNotes()

            if (args[0] == "help") {
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
            
         To access the app through the command line, simply run ./app <ARG>
         
         The value of <ARG> can be:
            - help: To view the info of the app and list of all notes 
            - Note ID: ID of the target note that is intended to be opened
       
        +-------------------------------------------------------------------------------------+
    """.trimIndent()
                )
                for (note in notesList) {
                    println("${note.id} | ${note.title} | ${note.lastModified}")
                }
            } else {
                var noteId = args[0]
                notesList.find { it.id == noteId } ?:
                    throw IllegalArgumentException("[ERROR]: Invalid Note ID!")
                Application.launch(TextWindow()::class.java, "--note=${noteId}")
            }
        } else {
            if (args.size > 1) {
                throw IllegalArgumentException("[ERROR]: Wrong number of arguments provided!")
            }
        }
    } catch (err: IllegalArgumentException) {
        System.err.println(err.message)
        System.err.println("This app only expects one argument: 'help' or ID of a note.")
        System.err.println("To view a list of all local notes that can be opened via the command line, please use the 'help' argument")
    }
}