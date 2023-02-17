package notes.multi.utilities

import java.io.File
import java.io.InputStream

class Filemanager(private val dir: String, val name: String) {
    private val directory = File(dir)
    private val filepath = File("$dir/$name")
    private val listfiles = mutableListOf<File>()
    init {
        for (f in directory.listFiles()!!) {
            if (f.extension.lowercase() == "txt" || f.extension.lowercase() == "md") {
                listfiles.add(f)
            }
        }
    }

    //returns list of files
    fun files() : MutableList<File> {
        return listfiles
    }


    // create and write to that file
    fun writefile(line:String) {
        filepath.writeText(line)
        if (!listfiles.contains(filepath)) {
            listfiles.add(filepath)
        }
    }

    // opens and read the existing file
    fun openfile(): String {
        if (!listfiles.contains(filepath)) {
            return ""
        }
        val inputStream: InputStream = filepath.inputStream()

        return inputStream.bufferedReader().use { it.readText() }
    }

    fun deletefile():Boolean {
        listfiles.remove(filepath)
        return filepath.delete()
    }

}