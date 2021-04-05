package de.scaramangado.announcer.api

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("racetime.api")
class RacetimeProperties(

    var baseUrl: String? = null,
    var websocketBase: String? = null,
    var resolveTeams: Boolean = true
)
