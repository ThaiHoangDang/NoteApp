package notes.multi.utilities

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * An abstract class denoting a Note and all the relevant information about it
 * @param title Title of the note
 * @param text Text/Content of the note
 * @param author Author of the note
 * @param extension File extension of the note
 * @param dateCreated Creation date of the note
 * @param lastModified Last modification date of the note
 * @param location Location of the note
 */
class Note(var title: String? = "Untitled",
           var text: StringBuffer? = StringBuffer(""),
//           val author: String = "?", // User class?
           var dateCreated: String = LocalDate.now().toString(),
           var lastModified: String = LocalDateTime.now().toString()) {
    // update last modified date
}