package de.scaramangado.announcer.api.model

data class User(
    var id: String? = null,
    var fullName: String? = null,
    var name: String? = null,
    var discriminator: String? = null
)
