package notes.multi.utilities

import javafx.application.Application
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox
import javafx.scene.layout.AnchorPane
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseDragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Priority
import javafx.scene.web.HTMLEditor
import java.beans.EventHandler

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

    /**
     * Boolean value denoting whether console has been pressed
     */
    private var controlPressed = false

    override fun init() {
        super.init()
        val params = parameters
        paramsMap = params.named
    }

    override fun start(stage: Stage) {
        val path = paramsMap["text"]!!
        val filecontroller = FileManager(path, paramsMap["title"]!!)
        stage.setTitle(paramsMap["title"])
        val textarea = HTMLEditor()
        //textarea.setText(paramsMap["text"])
        textarea.htmlText = filecontroller.openFile()

        //textarea.setWrapText(true)


        val scroll = ScrollPane()
        val anchor = AnchorPane(textarea)

        AnchorPane.setTopAnchor(textarea, 0.0)
        AnchorPane.setBottomAnchor(textarea, 0.0)
        AnchorPane.setLeftAnchor(textarea, 0.0)
        AnchorPane.setRightAnchor(textarea, 0.0)

        scroll.isFitToHeight = true
        scroll.hmin = 300.0
        scroll.isFitToWidth = true

        /**
         * Responsive Design and scroll properties
         */
        scroll.content = textarea
        val box = VBox(anchor)
        VBox.setVgrow(anchor, Priority.ALWAYS)

        stage.scene = Scene(box, 300.0, 300.0)

        /**
         * Logic for key presses:
         * - Save: Ctrl + S
         * - Delete: Ctrl + D
         * - Close Window: Ctrl + W
         */
        stage.scene.setOnKeyPressed { event->
            if (event.code == KeyCode.CONTROL) {
                controlPressed = true
            } else if (event.code == KeyCode.S && controlPressed) {
                val warning = Alert(Alert.AlertType.CONFIRMATION)
                warning.title = "SAVE"
                warning.contentText = "Do you want to save this file?"
                val result = warning.showAndWait()
                if (result.isPresent) {
                    when (result.get()) {
                        ButtonType.OK -> filecontroller.writeFile(textarea.htmlText)
                    }
                }
            } else if (event.code == KeyCode.D && controlPressed) {
                val warning = Alert(Alert.AlertType.CONFIRMATION)
                warning.title = "DELETE"
                warning.contentText = "Do you delete this file?"
                val result = warning.showAndWait()
                if (result.isPresent) {
                    when (result.get()) {
                        ButtonType.OK -> {filecontroller.deleteFile()
                            Platform.exit()}
                    }
                }
            } else if (event.code == KeyCode.W && controlPressed) {
                val warning = Alert(Alert.AlertType.CONFIRMATION)
                warning.title = "WARNING"
                warning.contentText = "The current work will not be saved. Are you sure you want to quit?"
                val result = warning.showAndWait()
                if (result.isPresent) {
                    when (result.get()) {
                        ButtonType.OK -> {
                            Platform.exit()
                        }
                    }
                }
            }
        }

        /**
         * Control press logic
         */
        stage.scene.setOnKeyReleased {
            if (controlPressed) {controlPressed = false}
        }


        stage.show()
    }
}