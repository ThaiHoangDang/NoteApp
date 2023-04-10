package notes.multi.utilities

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
import javafx.stage.Modality
import javafx.stage.Stage
import java.time.LocalDateTime

class notescene(private val stage: Stage, private val lists:GUInote, private val id: String = "-1") {
    /**
     * Boolean value denoting whether console has been pressed
     */
    private var controlPressed = false

    private var newname = true
    private var curfile = Note()
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
        val modechange = Menu("Mode")
        val sync = Menu("Sync")

        // File menu items
        val new = MenuItem("New")
        val open = MenuItem("Local Notes")
        val openserver = MenuItem("Remote Notes")
        val save = MenuItem("Save")
        val rename = MenuItem("Rename")
        val delete = MenuItem("Delete")

        // Modechange menu items
        val dark = MenuItem("Dark")
        val light = MenuItem("Light")

        // option menu items
        val update = MenuItem("Update Remote")
        val fetch = MenuItem("Fetch Remote")

        new.setOnAction {
            val newwindow = Stage()
            notescene(newwindow, lists, id+1)
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

                browser.setOnCloseRequest {
                    lists.setbrowseropened(false)
                }
            } else {
                val warning = Alert(Alert.AlertType.ERROR)
                warning.title = "ERROR"
                warning.contentText = "The file browser is opened elsewhere"
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

                browser.show()

                browser.setOnCloseRequest {
                    lists.setbrowseropened(false)
                }
            } else {
                val warning = Alert(Alert.AlertType.ERROR)
                warning.title = "ERROR"
                warning.contentText = "The file browser is opened elsewhere"
                warning.showAndWait()
            }
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
                warning.showAndWait()
            }
        }

        update.setOnAction {
            if (!lists.isbrowseropened()) {
                val warning = Alert(Alert.AlertType.CONFIRMATION)
                warning.title = "UPDATE"
                warning.contentText = "Do you want to update the file to server?"
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
                warning.showAndWait()
            }
        }

        fetch.setOnAction {
            if (!lists.isbrowseropened()) {
                val warning = Alert(Alert.AlertType.CONFIRMATION)
                warning.title = "FETCH"
                warning.contentText = "Any unuploaded changes will be lost. Are you sure fetch?"
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
                warning.showAndWait()
            }
        }

        filemenu.items.addAll(new, open, openserver ,save, rename, delete)
        modechange.items.addAll(dark, light)
        sync.items.addAll(update, fetch)
        menubar.menus.addAll(filemenu, modechange, sync)

        val box = VBox(menubar, anchor)
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
                    warning.showAndWait()
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

    fun settitle(passedtitle:String) {
        stage.title = passedtitle
    }

    fun settext(content: String) {
        textarea.htmlText = content
    }

    fun setnewname(new: Boolean) {
        newname = new
    }
}