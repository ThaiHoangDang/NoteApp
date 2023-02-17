/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package notes.multi.console

import notes.multi.app.MessageUtils
import notes.multi.utilities.Filemanager
import java.lang.IllegalArgumentException

import notes.multi.utilities.Note
import notes.multi.utilities.TextWindow
import javafx.application.Application
import javafx.stage.Stage

fun main(args: Array<String>) {
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
            
         1. Create Notes
            - To create notes, users must type "notes <filename>"
            
         2. Edit Notes
            - To edit notes, users must type "notes <filename>"
            - If the file doesn't exist, a new note will be created
            
         3. Delete Notes
            - To delete a note, users must type "notes -d <filename>"
            - If the user tries to delete a note that doesn't exist,
              the app will display an error message
        +-------------------------------------------------------------------------------------+
    """.trimIndent()
        )
    } else {
        if (args.size < 2) {
            val filename = args[0]
            // Optional Regex Check for a specific argument format:
            MessageUtils.verifyFilename(filename, Regex("^.*[.](md|txt)$"))
            // if (fileExists) {
            //     Filemanager(".").editfile()
            // } else {
            //     Filemanager(".").createfile()
            // }
        } else {
            if (args[0] != "-d") {
                throw IllegalArgumentException("[ERROR]: First parameter must be the delete flag")
            } else {
                val filename = args[1]

                // Optional Regex Check for a specific argument format:
                MessageUtils.verifyFilename(filename, Regex("^.*[.](md|txt)$"))
                // if (fileExists) {
                //     Filemanager(".").deletefile()
                // } else {
                //     throw IllegalArgumentException("[ERROR]: File must exist for it to be deleted")
                // }
            }
        }
    }
}

// fun main() {
//     // thesea re test functions feel free to use
//     val n = Note()
//     val noteTitle = "testver2.txt"
//     val path = "${System.getProperty("user.dir")}/test/"
//     Application.launch(TextWindow()::class.java, "--title=${noteTitle}", "--text=${path}")
//
//     // val f = Filemanager("${System.getProperty("user.dir")}/test/", "hello.txt")
//     // f.writefile("hello world")
//     // f.deletefile()
//     // println(f.openfile())
// }

