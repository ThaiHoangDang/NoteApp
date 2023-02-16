package notes.multi.utilities

import javafx.application.Application
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox
import javafx.application.Application.Parameters

class TextWindow(): Application() {
    var paramsMap = mutableMapOf<String, String>()
    override fun init() {
        super.init()
        val params = getParameters()
        paramsMap = params.getNamed()
    }

    override fun start(stage: Stage) {
        stage.setTitle(paramsMap["title"])
        val textarea = TextArea()
        textarea.setText(paramsMap["text"])
        textarea.setWrapText(true)
        val scroll = ScrollPane()

        scroll.setFitToHeight(true)
        scroll.setHmin(300.0)
        scroll.setFitToWidth(true)

        // REMOVE THESE COMMENTS
        // println("===========")
        // println(scroll.isFitToHeight)
        // println(scroll.isFitToWidth)

        scroll.content = textarea

        stage.scene = Scene(VBox(scroll), 300.0, 300.0)
        stage.show()
    }
}