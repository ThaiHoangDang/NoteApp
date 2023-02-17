package notes.multi.utilities

import java.io.File
import java.io.InputStream

/**
 * Used for managing files within the notes application and performs create, delete, and edit operations on files
 * @param dir Directory of the file
 * @param name Name of the file
 */
class FileManager(private val dir: String, val name: String) {
    private val directory = File(dir)
    private val filepath = File("$dir/$name")
    private val listFiles = mutableListOf<File>()
    init {
        for (f in directory.listFiles()!!) {
            if (f.extension.lowercase() == "txt" || f.extension.lowercase() == "md") {
                listFiles.add(f)
            }
        }
    }

    /**
     * Returns a list of mutable files
     */
    fun files() : MutableList<File> {
        return listFiles
    }


    /**
     * Create file and write to it
     */
    fun writeFile (line : String) {
        filepath.writeText(line)
        if (!listFiles.contains(filepath)) {
            listFiles.add(filepath)
        }
    }

    /**
     * Open and read existing file
     */
    fun openFile(): String {
        if (!listFiles.contains(filepath)) {
            return ""
        }
        val inputStream: InputStream = filepath.inputStream()

        return inputStream.bufferedReader().use { it.readText() }
    }

    /**
     * Delete file
     */
    fun deleteFile(): Boolean {
        listFiles.remove(filepath)
        return filepath.delete()
    }

}