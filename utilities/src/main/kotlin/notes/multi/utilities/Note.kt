package notes.multi.utilities

import java.time.LocalDate
import java.time.LocalDateTime


class Note(var title: String = "Untitled",
           var text: StringBuffer = StringBuffer(""),
           val author: String = "?", // User class?
           var extension: String= "?", // necessary?
           val dateCreated: LocalDate? = LocalDate.now(),
           var lastModified: LocalDateTime? = LocalDateTime.now(),
           var location: String? = null) { // path to the created file?
    // update last modified date
    // images?
}