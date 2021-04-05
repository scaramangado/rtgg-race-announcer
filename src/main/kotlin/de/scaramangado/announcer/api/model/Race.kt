package de.scaramangado.announcer.api.model

data class Race(
    var version: Int = 0,
    var name: String,
    var status: RaceStatus,
    var url: String = "https://racetime.gg",
    var goal: RaceGoal,
    var info: String = "",
    var entrants: List<Entrant>,
    var teamRace: Boolean = false,
) {
  val slug: String get() = name.split("/")[1]
}
