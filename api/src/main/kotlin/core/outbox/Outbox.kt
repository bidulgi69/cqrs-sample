package core.outbox

data class Outbox(
    val id: Long? = null,
    val topic: String,
    val eventAsJson: String,
)