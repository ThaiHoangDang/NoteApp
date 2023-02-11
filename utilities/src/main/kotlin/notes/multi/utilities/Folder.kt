package notes.multi.utilities

import java.time.LocalDate
import java.time.LocalDateTime

class Folder(var title: String = "Untitled",
             var description: String = "Empty",
             val author: String = "?", // User class?
             val dateCreated: LocalDate? = LocalDate.now(),
             var lastModified: LocalDateTime? = LocalDateTime.now(),
             var notes: MutableList<Note>? = null) {

    // add/remove notes
    // update last modified
}