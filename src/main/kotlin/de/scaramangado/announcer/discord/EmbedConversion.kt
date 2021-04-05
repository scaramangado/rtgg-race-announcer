package de.scaramangado.announcer.discord

import de.scaramangado.announcer.api.model.Entrant
import de.scaramangado.announcer.api.model.EntrantStatus
import de.scaramangado.announcer.api.model.Race
import de.scaramangado.announcer.api.model.RaceStatus
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

    if (!info.isEmpty()) {
      append("**Goal**\n$info\n\n")
    }

    if (entrants.isNotEmpty()) {
      append("**Entrants**\n")
      entrants
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
        EntrantStatus.Status.REQUESTED -> "*Requested to join*"
        EntrantStatus.Status.INVITED -> "*Invited*"
        EntrantStatus.Status.DECLINED -> "*Declined invitation*"
        EntrantStatus.Status.READY -> "*Ready*"
        EntrantStatus.Status.NOT_READY -> "*Not ready*"
        EntrantStatus.Status.IN_PROGRESS -> "*Playing*"
        EntrantStatus.Status.DONE -> finishTime?.standardFormat() ?: ""
        EntrantStatus.Status.DNF -> "*Forfeit*"
        EntrantStatus.Status.DQ -> "*DQ*"
        null -> "*unknown*"
      }
  )
  append("\n")
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
  get() = when(status.value) {
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
