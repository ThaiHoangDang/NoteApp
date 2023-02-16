package notes.multi.utilities

import javafx.application.Application
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox


class TextWindow(val note: Note): Application() {
    override fun init() {
        super.init()
    }
    override fun start(stage: Stage?) {
        stage?.setTitle(note.title)
        val textarea = TextArea()
        textarea.setText(note.text.toString())
        textarea.setWrapText(true)
        val scroll = ScrollPane()
        scroll.content = textarea
        stage?.scene = Scene(VBox(scroll), 300.0, 300.0)
        stage?.show()
    }
}