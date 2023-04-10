/**
 * Unless explicitly stated otherwise all files in this repository are licensed under the MIT License
 * Copyright (c) 2023 Abhay Menon, Inseo Kim, Hoang Dang, Guransh Khurana, Anshul Ruhil
 */

package notes.multi.utilities

import java.time.LocalDateTime

class LastSave(
    var id: String = "1",
    var lastUpdate: String = LocalDateTime.now().toString()
) {}

class SyncRequest(
    var lastUpdate: String,
    var records: MutableList<RemoteNote>
) {}