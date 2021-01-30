package de.scaramangado.announcer.races

import de.scaramangado.announcer.api.RacetimeProperties
import de.scaramangado.announcer.api.model.Race
import de.scaramangado.announcer.discord.DiscordManager
import de.scaramangado.announcer.discord.toEmbed
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class RaceManager(private val properties: RacetimeProperties, private val discordManager: DiscordManager,
                  private val raceRepository: RaceRepository) {

  private val logger = LoggerFactory.getLogger(RaceManager::class.java)
  private val races = mutableMapOf<String, AnnouncedRace>()

  init {
    raceRepository.load()
        .also {
          logger.info("Loaded ${it.size} active races")
        }.forEach {
          newRaceConnection(it)
        }
  }

  fun liveRaces(liveRaces: Collection<Race>) {
    liveRaces
        .filter { it.slug !in races.keys }
        .forEach { announceRace(it) }
  }

  private fun announceRace(race: Race) {
    logger.info("Announce new race ${race.slug}")
    newRaceConnection(AnnouncedRace(race.slug))
  }

  private fun updateMessage(race: Race) {

    try {
      val announcement = races[race.slug] ?: return logMissingRace(race.slug)

      logger.trace("Update announcement ${race.slug}")

      if (announcement.messageId.isEmpty()) {
        announcement.messageId = discordManager.sendMessage(race.toEmbed())
      } else if (race.version > announcement.latestVersion) {
        discordManager.editMessage(announcement.messageId, race.toEmbed())
      }

      announcement.latestVersion = race.version
      updateRepository()
    } catch (e: Exception) {
      logger.error("Failed to update message. ${e.javaClass.simpleName}: ${e.message}")
      logger.trace("", e)
    }
  }

  private fun removeRace(race: Race) {
    logger.info("Removing ${race.slug} from active races")
    races.remove(race.slug)
    updateRepository()
  }

  private fun newRaceConnection(race: AnnouncedRace) {
    races[race.raceSlug] = race
    RaceConnection("${properties.websocketBase}/ws/race/${race.raceSlug}", { updateMessage(it) }, { removeRace(it) })
  }

  private fun updateRepository() {
    raceRepository.save(races.values)
  }

  private fun logMissingRace(slug: String) {
    logger.error("Tried to update unknown race $slug")
  }
}
