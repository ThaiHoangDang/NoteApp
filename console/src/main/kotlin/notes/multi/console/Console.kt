/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package notes.multi.console

import notes.multi.utilities.Filemanager

fun main() {
    // thesea re test functions feel free to use
    val f = Filemanager("${System.getProperty("user.dir")}/test/", "hello.txt")
    f.writefile("hello world")
   println(f.openfile())
    f.deletefile()
}
