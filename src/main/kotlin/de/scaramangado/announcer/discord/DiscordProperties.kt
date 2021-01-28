package de.scaramangado.announcer.discord

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("discord")
class DiscordProperties(
    var token: String? = null,
    var channelId: String? = null,
    var guilId: Long = 0
)
