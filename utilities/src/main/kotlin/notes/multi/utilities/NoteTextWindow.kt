package notes.multi.utilities

import com.beust.klaxon.Klaxon
import javafx.application.Application
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.scene.layout.AnchorPane
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.web.HTMLEditor
import javafx.stage.Modality
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * - Displays a responsive `TextArea` in a window with the text of the file passed to the `Application.launch` function
 * - Parameters are passed through the second parameter of the `Application.launch` in this format:
 * ```kotlin
 * Application.launch(TextWindow()::class.java, "--title=${fileTitle}", "--location=${fileLocation}")
 * ```
 * @param title Name of the file to be accessed
 * @param location Location of the file to be accessed
 */
class TextWindow(): Application() {
    /**
     * Map of params received by the `Application.Launch` function
     * @see /console/src/main/kotlin/notes/multi/console/Console.kt
     */
    private var paramsMap = mutableMapOf<String, String>()


    override fun init() {
        super.init()
        val params = parameters
        paramsMap = params.named
    }

    override fun start(stage: Stage) {
        val noteslist = GUInote()
        val noteId = if (paramsMap.containsKey("note")) paramsMap["note"]!! else "-1"
        val newwindow = notescene(stage, noteslist, noteId)
    }
}