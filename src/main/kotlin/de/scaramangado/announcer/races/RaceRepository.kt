package de.scaramangado.announcer.races

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths

@Component
class RaceRepository(private val gson: Gson) {

  private val logger = LoggerFactory.getLogger(RaceRepository::class.java)
  private val dataFile = Paths.get("active_races.json")

  fun save(races: Collection<AnnouncedRace>) {
    try {
      Files.writeString(dataFile, gson.toJson(races))
    } catch (e: Exception) {
      logger.error("Failed to save races", e)
    }
  }

  fun load(): Collection<AnnouncedRace> {

    if (!Files.exists(dataFile)) {
      logger.info("Races file not found")
      return emptyList()
    }

    return try {
      gson.fromJson<List<AnnouncedRace>>(Files.readString(dataFile))
    } catch (e: Exception) {
      logger.error("Unable to read races", e)
      emptyList()
    }
  }

  private inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object : TypeToken<T>() {}.type)
}
