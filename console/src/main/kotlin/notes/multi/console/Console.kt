/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package notes.multi.console
import notes.multi.app.MessageUtils

import java.lang.IllegalArgumentException

import notes.multi.utilities.TextWindow
import javafx.application.Application
import kotlin.io.path.Path
import java.io.File


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
            - To create notes, users must type "notes <filename>"
            - To save changes, use the Ctrl + S keybinding
            
         2. Edit Notes
            - To edit notes, users must type "notes <filename>"
            - If the file doesn't exist, a new note will be created
            - To save changes, use the Ctrl + S keybinding
            
         3. Delete Notes
            - To delete a note, use the Ctrl + D keybinding
            
         4. Close GUI Window
            - To close the GUI window, use the Ctrl + W keybinding
       
        +-------------------------------------------------------------------------------------+
    """.trimIndent()
            )
        } else {
            if (args.size < 2) {


                val filePathArg = args[0]

                val fileTitle = Path(filePathArg).getFileName()
                val fileText = Path(filePathArg).getParent() ?: System.getProperty("user.dir")
                if (!File(fileText.toString()).isDirectory) throw IllegalArgumentException("[ERROR]: Directory does not exist!")

                // Regex Check for a specific argument format:
                MessageUtils.verifyFilename(fileTitle.toString(), Regex("^.*[.]([Mm][Dd]|[Tt][Xx][Tt])$"))

                val path = System.getProperty("user.dir")
                Application.launch(TextWindow()::class.java, "--title=${fileTitle}", "--text=${fileText}")

            } else {
                throw IllegalArgumentException("[ERROR]: Wrong number of arguments provided!")
            }
        }
    } catch (err: IllegalArgumentException) {
        System.err.println(err.message)
    }
}

