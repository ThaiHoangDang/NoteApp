package notes.multi.utilities
import notes.multi.utilities.Filemanager

import javafx.application.Application
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox
import javafx.scene.layout.AnchorPane
import javafx.application.Application.Parameters
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.input.KeyCode
import javafx.scene.layout.Priority

class TextWindow(): Application() {
    var paramsMap = mutableMapOf<String, String>()


    var controlpressed = false
    override fun init() {
        super.init()
        val params = getParameters()
        paramsMap = params.getNamed()
    }

    override fun start(stage: Stage) {
        val path = paramsMap["text"]!!
        val filecontroller = Filemanager(path, paramsMap["title"]!!)
        stage.setTitle(paramsMap["title"])
        val textarea = TextArea()
        //textarea.setText(paramsMap["text"])
        textarea.setText(filecontroller.openfile())
        textarea.setWrapText(true)
        val scroll = ScrollPane()
        val anchor = AnchorPane(textarea)

        AnchorPane.setTopAnchor(textarea, 0.0)
        AnchorPane.setBottomAnchor(textarea, 0.0)
        AnchorPane.setLeftAnchor(textarea, 0.0)
        AnchorPane.setRightAnchor(textarea, 0.0)

        scroll.setFitToHeight(true)
        scroll.setHmin(300.0)
        scroll.setFitToWidth(true)

        // REMOVE THESE COMMENTS
        // println("===========")
        // println(scroll.isFitToHeight)
        // println(scroll.isFitToWidth)

        scroll.content = textarea
        val box = VBox(anchor)
        VBox.setVgrow(anchor, Priority.ALWAYS)

        stage.scene = Scene(box, 300.0, 300.0)


        stage.scene.setOnKeyPressed { event->
            if (event.code == KeyCode.CONTROL) {
                controlpressed = true
            } else if (event.code == KeyCode.S && controlpressed) {
                val warning = Alert(Alert.AlertType.CONFIRMATION)
                warning.title = "SAVE"
                warning.contentText = "Do you want to save this file?"
                val result = warning.showAndWait()
                if (result.isPresent) {
                    when (result.get()) {
                        ButtonType.OK -> filecontroller.writefile(textarea.getText())
                    }
                }
            } else if (event.code == KeyCode.D && controlpressed) {
                val warning = Alert(Alert.AlertType.CONFIRMATION)
                warning.title = "DELETE"
                warning.contentText = "Do you delete this file?"
                val result = warning.showAndWait()
                if (result.isPresent) {
                    when (result.get()) {
                        ButtonType.OK -> {filecontroller.deletefile()
                            Platform.exit()}
                    }
                }
            } else if (event.code == KeyCode.W && controlpressed) {
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

        stage.scene.setOnKeyReleased { event->
            if (controlpressed) {controlpressed = false}
        }

        stage.show()
    }
}