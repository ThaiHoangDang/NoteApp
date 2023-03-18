package notes.multi.utilities

import javafx.beans.property.SimpleStringProperty
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * An abstract class denoting a Note and all the relevant information about it
 * @param id ID of the note
 * @param title Title of the note
 * @param text Text/Content of the note
 * @param author Author of the note
 * @param dateCreated Creation date of the note
 * @param lastModified Last modification date of the note
 */
class Note(
    var id: String = UUID.randomUUID().toString(),
    var title: String? = "Untitled",
    var text: StringBuffer? = StringBuffer(""),
//           val author: String = "?", // User class?
    var dateCreated: String = LocalDate.now().toString(),
    var lastModified: String = LocalDateTime.now().toString(),
    var titleGUI:SimpleStringProperty = SimpleStringProperty(title),
    var lastModifiedGUI: SimpleStringProperty = SimpleStringProperty(lastModified)
) {
}