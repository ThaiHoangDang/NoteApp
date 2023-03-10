package notes.multi.utilities

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * An abstract class denoting a Folder and all the relevant information about it
 * @param title Title of the folder
 * @param description Description of the folder
 * @param author Author of the folder
 * @param dateCreated Creation date of the folder
 * @param lastModified Last modification date of the folder
 * @param notes List of notes belonging to the folder
 */
class Folder(var title: String = "Untitled",
             var description: String = "Empty",
             val author: String = "?", // User class?
             val dateCreated: LocalDate? = LocalDate.now(),
             var lastModified: LocalDateTime? = LocalDateTime.now(),
             var notes: MutableList<Note>? = null) {
}