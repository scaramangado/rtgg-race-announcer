package de.scaramangado.announcer.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import org.springframework.stereotype.Component

@Component
class DiscordManager(private val properties: DiscordProperties) {

  private val jda: JDA = JDABuilder.createLight(properties.token).build()

  fun sendMessage(embed: MessageEmbed): String =
      channel.sendMessage(embed).submit().get().id

  fun editMessage(id: String, newEmbed: MessageEmbed) {
    channel.editMessageById(id, newEmbed).submit()
  }

  private val channel by lazy {

    Thread.sleep(2000)
    jda.textChannels.findLast { it.id == properties.channelId }!!
  }
}
