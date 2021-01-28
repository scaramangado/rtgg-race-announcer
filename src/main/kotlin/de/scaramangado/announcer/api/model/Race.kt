package de.scaramangado.announcer.api.model

import java.time.Duration
import java.time.Instant

data class Race(
    var version: Int = 0,
    var name: String,
    var status: RaceStatus,
    var url: String = "https://racetime.gg",
    var goal: RaceGoal,
    var info: String = "",
    var entrants: List<Entrant>
) {
  val slug: String get() = name.split("/")[1]
}
