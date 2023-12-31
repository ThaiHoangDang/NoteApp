/**
 * Unless explicitly stated otherwise all files in this repository are licensed under the MIT License
 * Copyright (c) 2023 Abhay Menon, Inseo Kim, Hoang Dang, Guransh Khurana, Anshul Ruhil
 */

package notes.multi.utilities

import javafx.beans.property.SimpleStringProperty
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class RemoteNote(
    var id: String = UUID.randomUUID().toString(),
    var title: String? = "Untitled",
    var text: String? = "",
//           val author: String = "?", // User class?
    var dateCreated: String = LocalDate.now().toString(),
    var lastModified: String = LocalDateTime.now().toString(),
) {
}