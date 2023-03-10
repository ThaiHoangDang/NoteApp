import notes.multi.utilities.FileManager
import kotlin.test.Test
import kotlin.test.assertEquals

internal class FileManagerTest {
    private val testmanager: FileManager = FileManager("${System.getProperty("user.dir")}/testfolder/", "hello.txt")

    @Test
    fun createreadfile() {
        val expected = "hello from the moon"
        testmanager.writeFile(expected)
        assertEquals(expected, testmanager.openFile())
    }

    @Test
    fun ReadNoneExistingFile() {
        val expected = ""
        testmanager.writeFile("walking to the mooon")
        testmanager.deleteFile()
        assertEquals(expected, testmanager.openFile())
    }
}