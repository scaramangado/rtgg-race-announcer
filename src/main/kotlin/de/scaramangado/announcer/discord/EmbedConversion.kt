package de.scaramangado.announcer.discord

import de.scaramangado.announcer.api.model.Race
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.MessageEmbed
import java.awt.Color
import java.time.Instant
import java.time.OffsetDateTime

fun Race.toEmbed(): MessageEmbed =
  EmbedBuilder()
      .setTitle(slug, "https://racetime.gg$url")
      .setDescription("")
      .setTimestamp(Instant.now())
      .setColor(Color.RED)
      .build()
