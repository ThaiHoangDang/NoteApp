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