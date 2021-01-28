package de.scaramangado.announcer.api.model

import java.time.Duration

data class Entrant(
    var user: User? = null,
    var status: EntrantStatus? = null,
    var finishTime: Duration? = null,
    var place: Int? = null
)
