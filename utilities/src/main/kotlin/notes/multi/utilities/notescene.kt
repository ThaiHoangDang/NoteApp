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

class notescene(private val stage: Stage, private val lists:GUInote, private val id:Int) {
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
        lists.addstage(this)

        stage.setOnCloseRequest {
            lists.removenote(curfile.id)
            lists.removestage(this)
        }
        stage.title = "Untitled"
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
        val option = Menu("Option")

        // File menu items
        val new = MenuItem("New")
        val open = MenuItem("Open")
        val save = MenuItem("Save")
        val rename = MenuItem("Rename")
        val delete = MenuItem("Delete")

        // Modechange menu items
        val dark = MenuItem("Dark")
        val light = MenuItem("Light")

        // option menu items
        val sync = MenuItem("Sync")

        new.setOnAction {
            val newwindow = Stage()
            notescene(newwindow, lists, id+1)
        }


        open.setOnAction {
            lists.setbrowseropened(true)
            val browser = Stage()
            browser.initModality(Modality.WINDOW_MODAL)
            browser.initOwner(stage)
            //lists.setowner(browser)
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
                        if (lists.findopened(obsfs[index].id)) {
                            browser.close()
                            lists.focusstage(obsfs[index].id)
                        } else {
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

        sync.setOnAction {
            if (!lists.isbrowseropened()) {
                val warning = Alert(Alert.AlertType.CONFIRMATION)
                warning.title = "SYNC"
                warning.contentText = "After synchronization, your current work may be lost. Are you sure sync?"
                val result = warning.showAndWait()
                if (result.isPresent) {
                    when (result.get()) {
                        ButtonType.OK -> {
                            if (DatabaseOperations.sync()) {
                                val success = Alert(Alert.AlertType.INFORMATION)
                                success.title = "SUCCESS"
                                success.contentText = "Sync success!"
                                lists.update()
                                success.showAndWait()
                            } else {
                                val warning = Alert(Alert.AlertType.ERROR)
                                warning.title = "ERROR"
                                warning.contentText = "Sync failed due to no internet."
                                warning.showAndWait()
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

        filemenu.items.addAll(new, open, save, rename, delete)
        modechange.items.addAll(dark, light)
        option.items.addAll(sync)
        menubar.menus.addAll(filemenu, modechange, option)

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