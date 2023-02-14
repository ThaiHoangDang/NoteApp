import notes.multi.utilities.Filemanager
import kotlin.test.Test
import kotlin.test.assertEquals

internal class FilemanagerTest {
    private val testmanager: Filemanager = Filemanager("${System.getProperty("user.dir")}/testfolder/", "hello.txt")

    @Test
    fun createreadfile() {
        val expected = "hello from the moon"
        testmanager.writefile(expected)
        assertEquals(expected, testmanager.openfile())
    }

    @Test
    fun ReadNoneExistingFile() {
        val expected = ""
        testmanager.writefile("walking to the mooon")
        testmanager.deletefile()
        assertEquals(expected, testmanager.openfile())
    }
}