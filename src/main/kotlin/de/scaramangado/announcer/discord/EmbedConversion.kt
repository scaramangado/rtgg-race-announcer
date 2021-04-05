package de.scaramangado.announcer.discord

import de.scaramangado.announcer.api.model.*
import de.scaramangado.announcer.api.model.EntrantStatus.Status.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import java.awt.Color
import java.time.Duration
import java.time.Instant

fun Race.toEmbed(): MessageEmbed =
    EmbedBuilder()
        .setTitle(goal.name, "https://racetime.gg$url")
        .setDescription(description)
        .setTimestamp(Instant.now())
        .setColor(status.toColor())
        .setFooter(footer)
        .build()

private val Race.description
  get() = buildString {

    if (info.isNotEmpty()) {
      append("**Goal**\n$info\n\n")
    }

    if (entrants.isNotEmpty()) {
      append("**Entrants**\n")
      effectiveEntrantList
          .asSequence()
          .sortedBy { it.place ?: 9999 }
          .take(20)
          .forEach { append(it.asEmbedLine()) }
      if (entrants.size > 20) {
        append("...\n")
      }
    }
  }

private fun Entrant.asEmbedLine() = buildString {
  place?.run { append("**$this.** ") }
  append("**${user?.name}** ")
  append(
      when (status?.value) {
        REQUESTED -> "*Requested to join*"
        INVITED -> "*Invited*"
        DECLINED -> "*Declined invitation*"
        READY -> "*Ready*"
        NOT_READY -> "*Not ready*"
        IN_PROGRESS -> "*Playing*"
        DONE -> finishTime?.standardFormat() ?: ""
        DNF -> "*Forfeit*"
        DQ -> "*DQ*"
        null -> "*unknown*"
      }
  )
  append("\n")
}

private val Race.effectiveEntrantList: List<Entrant>
  get() {
    return if (teamRace) extractTeams() else entrants
  }

private fun Race.extractTeams(): List<Entrant> {

  fun Map.Entry<Team, List<Entrant>>.convertToEntrant(): Entrant {

    val name =
        if (key.formal) key.name else value.joinToString { it.user?.name ?: "" }

    val status = when {
      value.any { it.status?.value == INVITED } -> INVITED
      value.any { it.status?.value == REQUESTED } -> REQUESTED
      value.any { it.status?.value == NOT_READY } -> NOT_READY
      value.all { it.status?.value == READY } -> READY
      value.any { it.status?.value == IN_PROGRESS } -> IN_PROGRESS
      value.all { it.status?.value == DNF } -> DNF
      value.all { it.status?.value in listOf(DONE, DNF) } -> DONE
      value.any { it.status?.value == DQ } -> DQ
      else -> NOT_READY
    }

    println("${key.name}: ${value.map { it.status?.value?.name }.joinToString { it ?: "null" }} => ${status.name}")

    val time = if (status != DONE) null else value.mapNotNull { it.finishTime }.maxOrNull()

    return Entrant(User(name = name), status = EntrantStatus(value = status), finishTime = time)
  }

  fun List<Entrant>.addPlaces(): List<Entrant> {

    val sorted = this.sortedWith(nullsLast(compareBy { it.finishTime }))

    var currentPlace = 1

    sorted
        .filter { it.finishTime != null }
        .forEach { it.place = currentPlace++ }

    return sorted
  }

  val teams = mutableMapOf<Team, List<Entrant>>()

  entrants
      .filter { it.team != null }
      .forEach {
        val team = requireNotNull(it.team)
        teams[team] =
            if (teams[team] == null) {
              listOf(it)
            } else {
              teams[team]!! + it
            }
      }

  return teams.map { it.convertToEntrant() }.addPlaces()
}

fun Duration.standardFormat(): String {
  return String.format("%d:%02d:%02d", this.toHours(), this.toMinutesPart(), this.toSecondsPart())
}

private fun RaceStatus.toColor(): Color {
  return when (value) {
    RaceStatus.Status.OPEN -> Color.GREEN
    RaceStatus.Status.INVITATIONAL -> Color.BLUE
    RaceStatus.Status.PENDING -> Color.YELLOW
    RaceStatus.Status.IN_PROGRESS -> Color.YELLOW
    RaceStatus.Status.FINISHED -> Color.RED
    RaceStatus.Status.CANCELLED -> Color.BLACK
    null -> Color.BLACK
  }
}

private val Race.footer
  get() = when (status.value) {
    RaceStatus.Status.OPEN -> "Open"
    RaceStatus.Status.INVITATIONAL -> "Invitational"
    RaceStatus.Status.PENDING -> "Starting..."
    RaceStatus.Status.IN_PROGRESS -> "In progress"
    RaceStatus.Status.FINISHED -> "Race Over"
    RaceStatus.Status.CANCELLED -> "Cancelled"
    null -> ""
  }.let {
    if (teamRace) {
      "Team Race ($it)"
    } else {
      it
    }
  }
