package notes.multi.utilities

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javax.swing.text.html.StyleSheet

fun toggleDarkMode(scene: Scene, isDarkMode: Boolean) {
    //val darktheme: StyleSheet = StyleSheet(false, "notes/multi/utilities/darktheme.css")

    if (isDarkMode) {
        //note.text.toString(). = Color.WHITE
        scene.root.style = "-fx-background-color: ${Color.BLACK.toString().replace("0x", "#")}; -fx-text-fill: ${Color.WHITE.toString().replace("0x", "#")}"
    } else {
        //note.text = Color.BLACK
        scene.root.style = "-fx-background-color: ${Color.WHITE.toString().replace("0x", "#")}; -fx-text-fill: ${Color.BLACK.toString().replace("0x", "#")}"
    }
    //isDarkMode = !isDarkMode
}