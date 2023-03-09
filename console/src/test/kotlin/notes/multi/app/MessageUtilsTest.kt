/*
 * This Kotlin source file was generated by the Gradle "init" task.
 */
package notes.multi.app

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

import notes.multi.utilities.Note
import notes.multi.utilities.Folder

import java.time.LocalDate
import java.time.LocalDateTime

class MessageUtilsTest {
    @Test fun testGetMessage() {
        assertEquals("Hello      World!", MessageUtils.getMessage())
    }
    @Test fun checkModelClasses() {
        var n = Note(
            title = "NeverGonnaGiveYouUp",
            text = StringBuffer(""),
            author = "Rick Astley",
            extension = "txt",
            lastModified = LocalDateTime.now(),
            location = "."
        )


        var fldr = Folder(
            title = "Test Folder",
            description = "This folder is a test",
            author = "Jeff Avery",
            dateCreated = LocalDate.now(),
            lastModified = null,
            notes = mutableListOf<Note>(n)
        )

        assertEquals(fldr.notes?.get(0) ?: null, n)
    }
}