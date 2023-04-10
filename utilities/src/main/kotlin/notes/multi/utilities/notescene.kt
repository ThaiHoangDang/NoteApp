package notes.multi.utilities

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
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

class notescene(private val stage: Stage, private val lists:GUInote, private val id: String = "-1") {
    /**
     * Boolean value denoting whether console has been pressed
     */
    private var controlPressed = false

    private var newname = true
    private var curfile = Note()
    private var isDarkMode = false
    private var textarea = HTMLEditor()

    fun file() :Note{
        return curfile
    }

    fun retstage() : Stage {
        return stage
    }

    fun rettextarea() : HTMLEditor {
        return textarea
    }


    init {


        if (id != "-1") {
            curfile = DatabaseOperations.getNote(id)
            textarea.htmlText = curfile.text.toString()
            newname = false
        }

        lists.addstage(this)

        stage.setOnCloseRequest {
            lists.removenote(curfile.id)
            lists.removestage(this)
        }
        stage.title = if (id != "-1") curfile.title else "Untitled"

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
        val sync = Menu("Sync")

        // File menu items
        val new = MenuItem("New")
        val open = MenuItem("Local Notes")
        val openserver = MenuItem("Remote Notes")
        val save = MenuItem("Save")
        val rename = MenuItem("Rename")
        val delete = MenuItem("Delete")

        //Insert menu items
        val insertimage = MenuItem("Insert Image")

        // Modechange menu items
        val dark = MenuItem("Dark")
        val light = MenuItem("Light")

        // option menu items
        val update = MenuItem("Update Remote")
        val fetch = MenuItem("Fetch Remote")

        new.setOnAction {
            val newwindow = Stage()
            notescene(newwindow, lists, "-1")
        }


        open.setOnAction {
            if (!lists.isbrowseropened()) {
                lists.setbrowseropened(true)
                val browser = Stage()
                browser.initModality(Modality.WINDOW_MODAL)
                browser.initOwner(stage)
                //lists.setowner(browser)
                val noteslist = DatabaseOperations.getAllNotes()
                val obsfs = FXCollections.observableArrayList<Note>()
                obsfs.addAll(noteslist)
                val titlecolumn = TableColumn<Note, String>("title")
                titlecolumn.setCellValueFactory { it -> it.value.titleGUI }
                val datecolumn = TableColumn<Note, String>("date modified")
                datecolumn.setCellValueFactory { it -> it.value.lastModifiedGUI }
                val notesview = TableView<Note>()
                notesview.items = obsfs
                notesview.columns.addAll(titlecolumn, datecolumn)
                notesview.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY


                notesview.setOnMouseClicked { event ->
                    val index = notesview.selectionModel.selectedIndex
                    if (index != -1) {
                        if (event.clickCount == 2) {
                            if (lists.findopened(obsfs[index].id)) {
                                if(isDarkMode){
                                    //textarea.stylesheets.add("newFile.css")
                                    toggleDarkMode(stage.scene, textarea, isDarkMode)
                                    isDarkMode = !isDarkMode
                                }
                                browser.close()
                                lists.focusstage(obsfs[index].id)
                            } else {
                                lists.removenote(curfile.id)
                                val tempnote = DatabaseOperations.getNote(obsfs[index].id)
                                textarea.htmlText = tempnote.text.toString()
                                stage.title = tempnote.title
                                browser.close()
                                curfile = tempnote
                                newname = false
                                lists.addnotes(tempnote.id)
                            }
                            lists.setbrowseropened(false)
                        }
                    }
                }

                val delete = Button("Delete")
                val open = Button("Open")

                open.setOnAction {
                    val index = notesview.selectionModel.selectedIndex
                    if (index != -1) {
                        if (lists.findopened(obsfs[index].id)) {
                            if (isDarkMode) {
                                toggleDarkMode(stage.scene, textarea, isDarkMode)
                                isDarkMode = !isDarkMode
                            }
                            browser.close()
                            lists.focusstage(obsfs[index].id)
                        } else {
                            lists.removenote(curfile.id)
                            val tempnote = DatabaseOperations.getNote(obsfs[index].id)
                            textarea.htmlText = tempnote.text.toString()
                            stage.title = tempnote.title
                            browser.close()
                            curfile = tempnote
                            newname = false
                            lists.addnotes(tempnote.id)
                        }
                        lists.setbrowseropened(false)
                    }
                }

                delete.setOnAction {
                    val index = notesview.selectionModel.selectedIndex
                    if (index != -1) {
                        val tempnote = DatabaseOperations.getNote(obsfs[index].id)
                        if (lists.findopened(obsfs[index].id)) {
                            val warning = Alert(Alert.AlertType.ERROR)
                            warning.title = "ERROR"
                            warning.contentText = "This file is opened in program"
                            // val dialogPane: DialogPane = warning.dialogPane
                            // if (isDarkMode) {
                            //     dialogPane.stylesheets.add("alertStylesheet.css")
                            // } else {
                            //     dialogPane.stylesheets.remove("alertStylesheet.css")
                            // }

                            warning.showAndWait()
                        } else {
                            val warningdel = Alert(Alert.AlertType.CONFIRMATION)
                            warningdel.title = "DELETE"
                            warningdel.contentText = "Do you delete this file?"
                            // val dialogPane: DialogPane = warningdel.dialogPane
                            // if (isDarkMode) {
                            //     dialogPane.stylesheets.add("alertStylesheet.css")
                            // } else {
                            //     dialogPane.stylesheets.remove("alertStylesheet.css")
                            // }

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

                if (isDarkMode) {
                    addDarkModeBrowser(browser.scene)
                } else {
                    removeDarkModeBrowser(browser.scene)
                }

                browser.show()

                browser.setOnCloseRequest {
                    lists.setbrowseropened(false)
                }
            } else {
                val warning = Alert(Alert.AlertType.ERROR)
                warning.title = "ERROR"
                warning.contentText = "The file browser is opened elsewhere"

                // val dialogPane: DialogPane = warning.dialogPane
                // if (isDarkMode) {
                //     dialogPane.stylesheets.add("alertStylesheet.css")
                // } else {
                //     dialogPane.stylesheets.remove("alertStylesheet.css")
                // }

                warning.showAndWait()
            }
        }

        openserver.setOnAction {
            if (!lists.isbrowseropened()) {
                lists.setbrowseropened(true)
                val browser = Stage()
                browser.initModality(Modality.WINDOW_MODAL)
                browser.initOwner(stage)
                //lists.setowner(browser)
                var noteslist = mutableListOf<Note>()
                try {
                    noteslist = DatabaseOperations.getAllServerNotes()
                } catch (e: Exception) {
                    val interneterror = Alert(Alert.AlertType.ERROR)
                    interneterror.title = "ERROR: NO INTERNET"
                    interneterror.contentText = "There is no internet to connect server"
                    // val dialogPane: DialogPane = interneterror.dialogPane
                    // if (isDarkMode) {
                    //     dialogPane.stylesheets.add("alertStylesheet.css")
                    // } else {
                    //     dialogPane.stylesheets.remove("alertStylesheet.css")
                    // }
                    interneterror.showAndWait()
                    browser.close()
                    lists.setbrowseropened(false)
                }
                val obsfs = FXCollections.observableArrayList<Note>()
                obsfs.addAll(noteslist)
                val titlecolumn = TableColumn<Note, String>("title")
                titlecolumn.setCellValueFactory { it -> it.value.titleGUI }
                val datecolumn = TableColumn<Note, String>("date modified")
                datecolumn.setCellValueFactory { it -> it.value.lastModifiedGUI }
                val notesview = TableView<Note>()
                notesview.items = obsfs
                notesview.columns.addAll(titlecolumn, datecolumn)
                notesview.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY


                val delete = Button("Delete")
                val fetch = Button("Fetch")

                fetch.setOnAction {
                    val index = notesview.selectionModel.selectedIndex
                    if (index != -1) {
                        DatabaseOperations.remotefetch(obsfs[index])
                        if (isDarkMode) {
                            //textarea.stylesheets.add("newFile.css")
                            toggleDarkMode(stage.scene, textarea, isDarkMode)
                            isDarkMode = !isDarkMode
                        }
                        lists.update()
                    }
                    val success = Alert(Alert.AlertType.INFORMATION)
                    success.title = "SUCCESS"
                    success.contentText = "Succesfully Fetched"
                    lists.update()
                    success.showAndWait()
                }

                delete.setOnAction {
                    val index = notesview.selectionModel.selectedIndex
                    if (index != -1) {
                        val tempnote = obsfs[index]
                        val warningdel = Alert(Alert.AlertType.CONFIRMATION)
                        warningdel.title = "DELETE"
                        warningdel.contentText = "Do you delete this file?"
                        // val dialogPane: DialogPane = warningdel.dialogPane
                        // if (isDarkMode) {
                        //     dialogPane.stylesheets.add("alertStylesheet.css")
                        // } else {
                        //     dialogPane.stylesheets.remove("alertStylesheet.css")
                        // }

                        val result = warningdel.showAndWait()
                        if (result.isPresent) {
                            when (result.get()) {
                                ButtonType.OK -> {
                                    DatabaseOperations.deleteRemote(tempnote.id)
                                    obsfs.removeAt(index)
                                }
                            }
                        }
                    }
                }

                val buttoncontainer = HBox(10.0, fetch, delete)

                val generalcontainer = VBox(notesview, buttoncontainer)
                VBox.setVgrow(notesview, Priority.ALWAYS)
                browser.scene = Scene(generalcontainer)
                if (isDarkMode) {
                    addDarkModeBrowser(browser.scene)
                } else {
                    removeDarkModeBrowser(browser.scene)
                }

                browser.show()

                browser.setOnCloseRequest {
                    lists.setbrowseropened(false)
                }
            } else {
                val warning = Alert(Alert.AlertType.ERROR)
                // val dialogPane: DialogPane = warning.dialogPane
                // if (isDarkMode) {
                //     dialogPane.stylesheets.add("alertStylesheet.css")
                // } else {
                //     dialogPane.stylesheets.remove("alertStylesheet.css")
                // }
                warning.title = "ERROR"
                warning.contentText = "The file browser is opened elsewhere"
                warning.showAndWait()
            }
        }

        save.setOnAction {
            val warning = Alert(Alert.AlertType.CONFIRMATION)
            warning.title = "SAVE"
            warning.contentText = "Do you want to save this file?"

            // val dialogPane: DialogPane = warning.dialogPane
            //
            // if(isDarkMode){
            //     dialogPane.stylesheets.add("alertStylesheet.css")
            //    // dialogPane.style = "-fx-background-color: black; -fx-text-background-color: white; -"
            //
            // }else{
            //     dialogPane.stylesheets.remove("alertStylesheet.css")
            //    // dialogPane.style = "-fx-background-color: white; -fx-text-background-color: black;"
            // }

            val result = warning.showAndWait()
            if (result.isPresent) {
                when (result.get()) {
                    ButtonType.OK -> {
                        if (newname) {
                            while(true) {
                                val renaming = TextInputDialog()
                                renaming.headerText = "Enter new name"
                                // if (isDarkMode) {
                                //     dialogPane.stylesheets.add("alertStylesheet.css")
                                // } else {
                                //     dialogPane.stylesheets.remove("alertStylesheet.css")
                                // }

                                val result = renaming.showAndWait()
                                if (result.isPresent) {
                                    if (renaming.editor.text == "") {
                                        val warning = Alert(Alert.AlertType.ERROR)
                                        warning.title = "ERROR"
                                        warning.contentText = "Empty can't be filename"
                                        // val dialogPane: DialogPane = warning.dialogPane
                                        // if (isDarkMode) {
                                        //     dialogPane.stylesheets.add("alertStylesheet.css")
                                        // } else {
                                        //     dialogPane.stylesheets.remove("alertStylesheet.css")
                                        // }

                                        warning.showAndWait()
                                    } else {
                                        stage.title = renaming.editor.text
                                        curfile.title = stage.title
                                        newname = false
                                        lists.addnotes(curfile.id)
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
                        // val dialogPane: DialogPane = warning.dialogPane
                        // if (isDarkMode) {
                        //     dialogPane.stylesheets.add("alertStylesheet.css")
                        // } else {
                        //     dialogPane.stylesheets.remove("alertStylesheet.css")
                        // }

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
            if (!lists.isbrowseropened()) {
                val warning = Alert(Alert.AlertType.CONFIRMATION)
                warning.title = "DELETE"
                warning.contentText = "Do you delete this file?"
                // val dialogPane: DialogPane = warning.dialogPane
                // if (isDarkMode) {
                //     dialogPane.stylesheets.add("alertStylesheet.css")
                // } else {
                //     dialogPane.stylesheets.remove("alertStylesheet.css")
                // }

                val result = warning.showAndWait()
                if (result.isPresent) {
                    when (result.get()) {
                        ButtonType.OK -> {
                            DatabaseOperations.deleteNote(curfile)
                            stage.close()
                        }
                    }
                }
            } else {
                val warning = Alert(Alert.AlertType.ERROR)
                warning.title = "ERROR"
                warning.contentText = "The file browser is opened elsewhere"
                // val dialogPane: DialogPane = warning.dialogPane
                // if (isDarkMode) {
                //     dialogPane.stylesheets.add("alertStylesheet.css")
                // } else {
                //     dialogPane.stylesheets.remove("alertStylesheet.css")
                // }

                warning.showAndWait()
            }
        }

        update.setOnAction {
            if (!lists.isbrowseropened()) {
                val warning = Alert(Alert.AlertType.CONFIRMATION)
                warning.title = "UPDATE"
                warning.contentText = "Do you want to update the file to server?"
                // val dialogPane: DialogPane = warning.dialogPane
                // if (isDarkMode) {
                //     dialogPane.stylesheets.add("alertStylesheet.css")
                // } else {
                //     dialogPane.stylesheets.remove("alertStylesheet.css")
                // }

                val result = warning.showAndWait()
                if (result.isPresent) {
                    when (result.get()) {
                        ButtonType.OK -> {
                            try {
                                val success = HttpOperations.addUpdateNote(curfile)
                                if (!success) {
                                    val warning = Alert(Alert.AlertType.ERROR)
                                    warning.title = "ERROR"
                                    warning.contentText = "Please save file locally before you upload."
                                    // val dialogPane: DialogPane = warning.dialogPane
                                    // if (isDarkMode) {
                                    //     dialogPane.stylesheets.add("alertStylesheet.css")
                                    // } else {
                                    //     dialogPane.stylesheets.remove("alertStylesheet.css")
                                    // }

                                    warning.showAndWait()
                                }
                            } catch (e: Exception) {
                                val interneterror = Alert(Alert.AlertType.ERROR)
                                interneterror.title = "ERROR: NO INTERNET"
                                interneterror.contentText = "There is no internet to connect server"
                                interneterror.showAndWait()
                            }
                        }
                    }
                }
            } else {
                val warning = Alert(Alert.AlertType.ERROR)
                warning.title = "ERROR"
                warning.contentText = "The file browser is opened elsewhere"
                // val dialogPane: DialogPane = warning.dialogPane
                // if (isDarkMode) {
                //     dialogPane.stylesheets.add("alertStylesheet.css")
                // } else {
                //     dialogPane.stylesheets.remove("alertStylesheet.css")
                // }

                warning.showAndWait()
            }
        }

        fetch.setOnAction {
            if (!lists.isbrowseropened()) {
                val warning = Alert(Alert.AlertType.CONFIRMATION)
                warning.title = "FETCH"
                warning.contentText = "Any unuploaded changes will be lost. Are you sure fetch?"
                // val dialogPane: DialogPane = warning.dialogPane
                // if (isDarkMode) {
                //     dialogPane.stylesheets.add("alertStylesheet.css")
                // } else {
                //     dialogPane.stylesheets.remove("alertStylesheet.css")
                // }

                val result = warning.showAndWait()
                if (result.isPresent) {
                    when (result.get()) {
                        ButtonType.OK -> {
                            try {
                                if(DatabaseOperations.localfetch(curfile)) {
                                    lists.update()
                                }
                            } catch (e: Exception) {
                                val interneterror = Alert(Alert.AlertType.ERROR)
                                interneterror.title = "ERROR: NO INTERNET"
                                interneterror.contentText = "There is no internet to connect server"
                                interneterror.showAndWait()
                            }
                        }
                    }
                }
            } else {
                val warning = Alert(Alert.AlertType.ERROR)
                warning.title = "ERROR"
                warning.contentText = "The file browser is opened elsewhere"
                // val dialogPane: DialogPane = warning.dialogPane
                // if (isDarkMode) {
                //     dialogPane.stylesheets.add("alertStylesheet.css")
                // } else {
                //     dialogPane.stylesheets.remove("alertStylesheet.css")
                // }

                warning.showAndWait()
            }
        }

        filemenu.items.addAll(new, open, openserver ,save, rename, delete)
        insertmenu.items.addAll(insertimage)
        modechange.items.addAll(dark, light)
        sync.items.addAll(update, fetch)
        menubar.menus.addAll(filemenu, insertmenu, modechange, sync)

        // adding images to the textarea
        // val selectImageButton = Button("Select Image")

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
                    if (responseIdx < 0) { throw ErrorInUploadingFile("Failed to add image to database. Response Code: ${-responseIdx}.")}
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
                // val dialogPane: DialogPane = warning.dialogPane
                // if (isDarkMode) {
                //     dialogPane.stylesheets.add("alertStylesheet.css")
                // } else {
                //     dialogPane.stylesheets.remove("alertStylesheet.css")
                // }

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
                                            // val dialogPane: DialogPane = warning.dialogPane
                                            // if (isDarkMode) {
                                            //     dialogPane.stylesheets.add("alertStylesheet.css")
                                            // } else {
                                            //     dialogPane.stylesheets.remove("alertStylesheet.css")
                                            // }

                                            warning.showAndWait()
                                        } else {
                                            stage.title = renaming.editor.text
                                            curfile.title = stage.title
                                            newname = false
                                            lists.addnotes(curfile.id)
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
                if (!lists.isbrowseropened()) {
                    val warning = Alert(Alert.AlertType.CONFIRMATION)
                    warning.title = "DELETE"
                    warning.contentText = "Do you delete this file?"
                    // val dialogPane: DialogPane = warning.dialogPane
                    // if (isDarkMode) {
                    //     dialogPane.stylesheets.add("alertStylesheet.css")
                    // } else {
                    //     dialogPane.stylesheets.remove("alertStylesheet.css")
                    // }

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

                                stage.close()
                            }
                        }
                    }
                } else {
                    val warning = Alert(Alert.AlertType.ERROR)
                    warning.title = "ERROR"
                    warning.contentText = "The file browser is opened elsewhere"
                    // val dialogPane: DialogPane = warning.dialogPane
                    // if (isDarkMode) {
                    //     dialogPane.stylesheets.add("alertStylesheet.css")
                    // } else {
                    //     dialogPane.stylesheets.remove("alertStylesheet.css")
                    // }

                    warning.showAndWait()
                }
            } else if (event.code == KeyCode.W && controlPressed) {
                controlPressed = false
                val warning = Alert(Alert.AlertType.CONFIRMATION)
                warning.title = "WARNING"
                warning.contentText = "The current work will not be saved. Are you sure you want to quit?"
                // val dialogPane: DialogPane = warning.dialogPane
                // if (isDarkMode) {
                //     dialogPane.stylesheets.add("alertStylesheet.css")
                // } else {
                //     dialogPane.stylesheets.remove("alertStylesheet.css")
                // }

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

        val wv = textarea.lookup("WebView")
        GridPane.setVgrow(wv, Priority.ALWAYS)

        stage.show()
    }

    fun settitle(passedtitle:String) {
        stage.title = passedtitle
    }

    fun settext(content: String) {
        textarea.htmlText = content
    }

    fun setnewname(new: Boolean) {
        newname = new
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
            return -res.code
        }

        if (res.body != null) {
            insertionIndex= res.body!!.string().toInt()
        }
        res.body?.close()

        return insertionIndex
    }
}