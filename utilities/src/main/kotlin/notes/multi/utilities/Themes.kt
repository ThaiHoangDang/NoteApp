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
import javafx.scene.web.HTMLEditor
import javafx.scene.web.WebView
import javax.swing.text.html.StyleSheet

fun toggleDarkMode(scene: Scene, editor: HTMLEditor, isDarkMode: Boolean) {
    //val darktheme: StyleSheet = StyleSheet(false, "notes/multi/utilities/darktheme.css")

    if (isDarkMode) {
        //note.text.toString(). = Color.WHITE
        //scene.root.style = "-fx-background-color: ${Color.BLACK.toString().replace("0x", "#")}; -fx-text-fill: ${Color.WHITE.toString().replace("0x", "#")}"
        editor.style = "-fx-background-color: white;"
        editor.htmlText = "<body style='background-color: white;'" + editor.htmlText
        scene.stylesheets.remove("darktheme.css")
    } else {
        //note.text = Color.BLACK
        //scene.root.style = "-fx-background-color: ${Color.WHITE.toString().replace("0x", "#")}; -fx-text-fill: ${Color.BLACK.toString().replace("0x", "#")}"
        editor.htmlText = "<body style='background-color: #121212;'" + editor.htmlText
        editor.style = "-fx-background-color: #121212;"
        scene.stylesheets.add("darktheme.css")


    }
    //isDarkMode = !isDarkMode
}

fun toggleDarkModeBrowser(scene: Scene,  isDarkMode: Boolean) {
    //val darktheme: StyleSheet = StyleSheet(false, "notes/multi/utilities/darktheme.css")
    if (isDarkMode) {
        //note.text.toString(). = Color.WHITE
        //scene.root.style = "-fx-background-color: ${Color.BLACK.toString().replace("0x", "#")}; -fx-text-fill: ${Color.WHITE.toString().replace("0x", "#")}
        scene.stylesheets.remove("darkthemeBrowser.css")
    } else {
        //note.text = Color.BLACK
        //scene.root.style = "-fx-background-color: ${Color.WHITE.toString().replace("0x", "#")}; -fx-text-fill: ${Color.BLACK.toString().replace("0x", "#")}
        scene.stylesheets.add("darkthemeBrowser.css")
    }
    //isDarkMode = !isDarkMode
}