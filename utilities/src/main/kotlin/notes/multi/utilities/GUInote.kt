/**
 * Unless explicitly stated otherwise all files in this repository are licensed under the MIT License
 * Copyright (c) 2023 Abhay Menon, Inseo Kim, Hoang Dang, Guransh Khurana, Anshul Ruhil
 */

package notes.multi.utilities

import javafx.stage.Stage

class GUInote {
    private val openednotes = mutableListOf<String>()
    private val notescenes = mutableListOf<notescene>()
    private var browseropened = false

    fun isbrowseropened() : Boolean {
        return browseropened
    }

    fun setbrowseropened(bo: Boolean) {
        browseropened = bo
    }

    fun addnotes(noteid:String) {
        openednotes.add(noteid)
    }

    fun removenote(noteid:String) {
        openednotes.remove(noteid)
    }

    fun findopened(noteid: String): Boolean {
        return openednotes.contains(noteid)
    }

    fun addstage(ns:notescene) {
        notescenes.add(ns)
    }

    fun removestage(ns: notescene) {
        notescenes.remove(ns)
    }

    fun focusstage(noteid:String) {
        for (i in notescenes) {
            if (i.file().id == noteid) {
                i.retstage().requestFocus()
                // println(i.retstage().isFocused)
                i.retstage().toFront()
            }
        }
    }

    fun update() {
        val noteslist = DatabaseOperations.getAllNotes()
        for (i in notescenes) {
            for (j in noteslist) {
                if (i.file().id == j.id) {
                    i.rettextarea().htmlText = j.text.toString()
                    i.retstage().title = j.title
                }
            }
        }
    }

}