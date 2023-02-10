package notes.multi.utilities

import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.io.path.exists
class Filemanager(val dir: String) {
    private val directory = File(dir)

    fun files() : MutableList<File> {
        val retfiles = mutableListOf<File>()
        for (f in directory.listFiles()!!) {
            if (f.extension == "txt" || f.extension == "md") {
                retfiles.add(f)
            }
        }
        return retfiles
    }


    fun createfile() {
        //TODO: Create File
    }

    fun deletefile(path:String) {
        //TODO: DELETE FILE
    }

}