package notes.multi.utilities

import com.beust.klaxon.JsonObject
import javafx.application.Application
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.web.HTMLEditor
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalDateTime
import java.util.*

// http://18.117.170.43:8080/notes-app-images/images/
// http://localhost:8080/images/
const val IMAGE_MICROSERVICE = "http://18.117.170.43:8080/notes-app-images/images/"

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

    private var newname = true
    private var curfile = Note()
    var isDarkMode = false

    private fun notesname() : MutableList<String> {
        val retlist = mutableListOf<String>()
        val temp = DatabaseOperations.getAllNotes()
        for (i in temp) {
            retlist.add(i.title?:"")
        }
        return retlist
    }

    override fun init() {
        super.init()
        val params = parameters
        paramsMap = params.named
    }

    override fun start(stage: Stage) {

        val textarea = HTMLEditor()
        //textarea.setText(paramsMap["text"])
        if (paramsMap.isNotEmpty()) {
            stage.setTitle(paramsMap["title"])
            textarea.htmlText = paramsMap["text"]
            newname = false
        } else {
            stage.setTitle("Untitled")
        }

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


        /**
         * MenuBar Design and implementation
         */

        val menubar = MenuBar()
        val filemenu = Menu("File")
        val insertmenu = Menu("Insert")
        val modechange = Menu("Mode")

        // File menu items
        val open = MenuItem("Open")
        val save = MenuItem("Save")
        val rename = MenuItem("Rename")
        val delete = MenuItem("Delete")

        //Insert menu items
        val insertimage = MenuItem("Insert Image")

        // Modechange menu items
        val dark = MenuItem("Dark")
        val light = MenuItem("Light")


        open.setOnAction {
            val browser = Stage()
            browser.initModality(Modality.WINDOW_MODAL)
            browser.initOwner(stage)
            val noteslist = DatabaseOperations.getAllNotes()
            val obsfs = FXCollections.observableArrayList<Note>()
            obsfs.addAll(noteslist)
            val titlecolumn = TableColumn<Note, String>("title")
            titlecolumn.setCellValueFactory { it-> it.value.titleGUI}
            val datecolumn = TableColumn<Note, String>("date modified")
            datecolumn.setCellValueFactory { it->it.value.lastModifiedGUI }
            val notesview = TableView<Note>()
            notesview.items = obsfs
            notesview.columns.addAll(titlecolumn, datecolumn)
            notesview.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY


            notesview.setOnMouseClicked { event->
                val index = notesview.selectionModel.selectedIndex
                if (index != -1) {
                    if (event.clickCount == 2) {
                        val tempnote = DatabaseOperations.getNote(obsfs[index].id)
                        textarea.htmlText = tempnote.text.toString()
                        stage.title = tempnote.title
                        browser.close()
                        curfile = tempnote
                        newname = false
                    }
                }
            }

            val delete = Button("Delete")
            val open = Button("Open")

            open.setOnAction {
                val index = notesview.selectionModel.selectedIndex
                if (index != -1) {
                        val tempnote = DatabaseOperations.getNote(obsfs[index].id)
                        textarea.htmlText = tempnote.text.toString()
                        stage.title = tempnote.title
                        browser.close()
                        curfile = tempnote
                        newname = false
                }
            }

            delete.setOnAction {
                val index = notesview.selectionModel.selectedIndex
                if (index != -1) {
                    val tempnote = DatabaseOperations.getNote(obsfs[index].id)
                    if (curfile.id == tempnote.id) {
                        val warning = Alert(Alert.AlertType.ERROR)
                        warning.title = "ERROR"
                        warning.contentText = "This file is opened in program"
                        warning.showAndWait()
                    } else {
                        val warningdel = Alert(Alert.AlertType.CONFIRMATION)
                        warningdel.title = "DELETE"
                        warningdel.contentText = "Do you delete this file?"
                        val result = warningdel.showAndWait()
                        if (result.isPresent) {
                            when (result.get()) {
                                ButtonType.OK -> {
                                    DatabaseOperations.deleteNote(tempnote)
                                    obsfs.removeAt(index)
                                }
                            }
                        }
                    }
                }
            }

            val buttoncontainer = HBox(10.0, open, delete)

            val generalcontainer = VBox(notesview, buttoncontainer)
            VBox.setVgrow(notesview, Priority.ALWAYS)
            browser.scene = Scene(generalcontainer)

            browser.show()
        }

        save.setOnAction {
            val warning = Alert(Alert.AlertType.CONFIRMATION)
            warning.title = "SAVE"
            warning.contentText = "Do you want to save this file?"
            val result = warning.showAndWait()
            if (result.isPresent) {
                when (result.get()) {
                    ButtonType.OK -> {
                        if (newname) {
                            while(true) {
                                val renaming = TextInputDialog()
                                renaming.headerText = "Enter new name"
                                val result = renaming.showAndWait()
                                if (result.isPresent) {
                                    if (renaming.editor.text == "") {
                                        val warning = Alert(Alert.AlertType.ERROR)
                                        warning.title = "ERROR"
                                        warning.contentText = "Empty can't be filename"
                                        warning.showAndWait()
                                    } else {
                                        stage.title = renaming.editor.text
                                        curfile.title = stage.title
                                        newname = false
                                        break
                                    }
                                } else {
                                    break
                                }
                            }
                        }

                        if(!newname) {
                            curfile.lastModified = LocalDateTime.now().toString()
                            curfile.text = StringBuffer(textarea.htmlText)
                            DatabaseOperations.addUpdateNote(curfile)
                        }
                    }
                }
            }
        }

        rename.setOnAction {
            while(true) {
                val renaming = TextInputDialog()
                renaming.headerText = "Enter new name"
                val result = renaming.showAndWait()
                if (result.isPresent) {
                    if (renaming.editor.text == "") {
                        val warning = Alert(Alert.AlertType.ERROR)
                        warning.title = "ERROR"
                        warning.contentText = "Empty can't be filename"
                        warning.showAndWait()
                    } else {
                        stage.title = renaming.editor.text
                        curfile.title = stage.title
                        curfile.lastModified = LocalDateTime.now().toString()
                        newname = false
                        break
                    }
                } else {
                    break
                }
            }
        }

        delete.setOnAction {
            val warning = Alert(Alert.AlertType.CONFIRMATION)
            warning.title = "DELETE"
            warning.contentText = "Do you delete this file?"
            val result = warning.showAndWait()
            if (result.isPresent) {
                when (result.get()) {
                    ButtonType.OK -> {
                        DatabaseOperations.deleteNote(curfile)
                        // filecontroller.deleteFile()

//                        Database.connect("jdbc:sqlite:test.db")
//                        transaction {
//                            SchemaUtils.create(DatabaseOperations.Notes)
//                            var deleteId = paramsMap["id"]?.toInt()
//                            if (deleteId !== null && deleteId != -1) {
//                                DatabaseOperations.deleteNote(deleteId)
//                            }
//                        }

                        Platform.exit()
                    }
                }
            }
        }




        filemenu.items.addAll(open, save, delete)
        insertmenu.items.addAll(insertimage)
        modechange.items.addAll(dark, light)
        menubar.menus.addAll(filemenu, insertmenu, modechange)

        //adding images to the textarea
        //val selectImageButton = Button("Select Image")

        insertimage.setOnAction {
            val filechooser = FileChooser()
            filechooser.title = "Select Image"
            filechooser.extensionFilters.addAll(
                    FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.jpeg")
            )
            val selectedImage = filechooser.showOpenDialog(null)
            if (selectedImage != null) {
                val imagePath = selectedImage.toURI().toString()
                try {
                    val responseIdx = sendImageToMicroservice(selectedImage)
                    if (responseIdx == -1) { throw ErrorInUploadingFile("There was an error in uploading the file!")}
                    val imageHTML = "<img src=${IMAGE_MICROSERVICE}${responseIdx} style=\'width: 100%;\'>"
                    textarea.htmlText += imageHTML
                } catch (e: IllegalFileTypeUpload) {
                    var warning = Alert(Alert.AlertType.ERROR)
                    warning.title = "Illegal file type"
                    warning.contentText = e.message
                    warning.showAndWait()
                } catch (e: ErrorInUploadingFile) {
                    var warning = Alert(Alert.AlertType.ERROR)
                    warning.title = "Error in file upload"
                    warning.contentText = e.message
                    warning.showAndWait()
                }
            }
        }

        //val image = textarea.lookup("#12345") as? WebView
        //var startX = 0.0
        //var startY = 0.0
        //var startWidth = 0.0
        //var startHeight = 0.0
        //var isResizing = false

        //image?.let { webView ->
        //    val webEngine = webView.engine

        //    webEngine.loadContent(textarea.htmlText)

        //    val document = webEngine.document
        //    val img = document.getElementById("12345")

        //    img?.let { element ->
        //        (element as HTMLElement).
        //    }
        //}

        val box = VBox(menubar, anchor)
        VBox.setVgrow(anchor, Priority.ALWAYS)

        stage.scene = Scene(box, 300.0, 300.0)


        dark.setOnAction {
            if (!isDarkMode) {
                toggleDarkMode(stage.scene, textarea, isDarkMode)
                isDarkMode = !isDarkMode
            }
        }

        light.setOnAction {
            if (isDarkMode) {
                toggleDarkMode(stage.scene, textarea, isDarkMode)
                isDarkMode = !isDarkMode
            }
        }

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
                controlPressed = false
                val warning = Alert(Alert.AlertType.CONFIRMATION)
                warning.title = "SAVE"
                warning.contentText = "Do you want to save this file?"
                val result = warning.showAndWait()
                if (result.isPresent) {
                    when (result.get()) {
                        ButtonType.OK -> {
                            if (newname) {
                                while(true) {
                                    val renaming = TextInputDialog()
                                    renaming.headerText = "Enter new name"
                                    val result = renaming.showAndWait()
                                    if (result.isPresent) {
                                        if (renaming.editor.text == "") {
                                            val warning = Alert(Alert.AlertType.ERROR)
                                            warning.title = "ERROR"
                                            warning.contentText = "Empty can't be filename"
                                            warning.showAndWait()
                                        } else {
                                            stage.title = renaming.editor.text
                                            curfile.title = stage.title
                                            newname = false
                                            break
                                        }
                                    } else {
                                        break
                                    }
                                }
                            }

                            if(!newname) {
                                curfile.lastModified = LocalDateTime.now().toString()
                                curfile.text = StringBuffer(textarea.htmlText)
                                DatabaseOperations.addUpdateNote(curfile)
                            }
                        }
                    }
                }
            } else if (event.code == KeyCode.D && controlPressed) {
                controlPressed = false
                val warning = Alert(Alert.AlertType.CONFIRMATION)
                warning.title = "DELETE"
                warning.contentText = "Do you delete this file?"
                val result = warning.showAndWait()
                if (result.isPresent) {
                    when (result.get()) {
                        ButtonType.OK -> {
                            DatabaseOperations.deleteNote(curfile)
                            // filecontroller.deleteFile()

//                            Database.connect("jdbc:sqlite:test.db")
//                            transaction {
//                                SchemaUtils.create(DatabaseOperations.Notes)
//                                var deleteId = paramsMap["id"]?.toInt()
//                                if (deleteId !== null && deleteId != -1) {
//                                    DatabaseOperations.deleteNote(deleteId)
//                                }
//                            }

                            Platform.exit()
                        }
                    }
                }
            } else if (event.code == KeyCode.W && controlPressed) {
                controlPressed = false
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

    class IllegalFileTypeUpload (msg: String) : Exception(msg)
    class ErrorInUploadingFile (msg: String) : Exception(msg)
    private fun sendImageToMicroservice(selectedImage: File): Int {
        val imgFile = File(selectedImage.toURI())

        val contentType = when (imgFile.extension.lowercase(Locale.getDefault())) {
            "jpeg", "jpg" -> "image/jpeg".toMediaTypeOrNull()
            "png" -> "image/png".toMediaTypeOrNull()
            "gif" -> "image/gif".toMediaTypeOrNull()
            else -> throw IllegalFileTypeUpload("Only .jpeg, .jpg, .png, and .gif file formats are accepted!")
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM).addFormDataPart(
                "image", imgFile.name,
                imgFile.asRequestBody(contentType)
            ).build()

        val request = Request.Builder()
            .url(IMAGE_MICROSERVICE)
            .post(requestBody)
            .build()

        val res = OkHttpClient().newCall(request).execute()
        var insertionIndex = -1
        if (!res.isSuccessful) {
            var warning = Alert(Alert.AlertType.ERROR)
            warning.title = "Image upload failed"
            warning.contentText = "Failed to add image to database. Response Code: ${res.code}."
            warning.showAndWait()
            return -1
        }

        if (res.body != null) {
            insertionIndex= res.body!!.string().toInt()
        }
        res.body?.close()

        return insertionIndex
    }
}