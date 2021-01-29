package de.scaramangado.announcer.races

import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import de.scaramangado.announcer.api.JsonConfiguration
import de.scaramangado.announcer.api.model.Race
import de.scaramangado.announcer.api.model.RaceStatus.Status.*
import org.slf4j.LoggerFactory
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import java.net.URI
import java.time.Duration
import java.time.Instant
import kotlin.concurrent.thread

class RaceConnection(private val raceEndpoint: String, private val messageUpdater: (Race) -> Unit) : WebSocketHandler {

  private val logger = LoggerFactory.getLogger(RaceConnection::class.java)

  private var raceStarted: Boolean = false
  private var firstConnect = true
  private var connectionErrorCount = 0
  private lateinit var session: WebSocketSession

  private val raceSlug: String
  private val opened = Instant.now()
  private val gson = JsonConfiguration().gson()

  init {
    StandardWebSocketClient()
        .doHandshake(this,
                     WebSocketHttpHeaders(),
                     URI.create(raceEndpoint))

    raceSlug = raceEndpoint.split("/").last()
  }

  override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {

    val payload = message.payload

    if (payload !is String || payload.contains("\"error\"")) {
      logger.info("Unusable payload in session $raceSlug: $payload")
      return
    }

    val racetimeMessage = gson.jsonToMap(payload)

    when (racetimeMessage["type"]) {
      "race.data" -> racetimeMessage.toRace()?.let { handleRaceEvent(it) }
      else -> return
    }
  }

  override fun afterConnectionEstablished(session: WebSocketSession) {
    this.session = session

    if (firstConnect) {
      logger.info("Opened connection $raceSlug")
    } else {
      logger.info("Successfully reconnected to race $raceSlug")
    }

    firstConnect = false
    connectionErrorCount = 0
  }

  private fun handleRaceEvent(race: Race) {
    messageUpdater(race)

    if (race.status.value in setOf(CANCELLED, FINISHED)) {
      thread {
        Thread.sleep(20000)
        logger.info("Closing connection to $raceSlug")
        session.close(CloseStatus.NORMAL)
      }
    }
  }

  //<editor-fold desc="Interface">

  override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
    logger.error("Error in session $raceSlug", exception)
    connectionErrorCount++
  }

  override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
    logger.info("connection $raceSlug closed after ${Duration.between(opened, Instant.now())}; $closeStatus")
    connectionErrorCount++

    if (connectionErrorCount < 10 && !raceStarted && closeStatus.code != 1000) {
      logger.info("Attempting reconnect...")
      StandardWebSocketClient()
          .doHandshake(this,
                       WebSocketHttpHeaders(),
                       URI.create(raceEndpoint))
    }
  }

  override fun supportsPartialMessages(): Boolean {
    return false
  }

  //</editor-fold>

  @Suppress("UNCHECKED_CAST")
  private fun Gson.jsonToMap(json: String): LinkedTreeMap<String, Any> =
      fromJson(json, Object::class.java) as LinkedTreeMap<String, Any>

  private fun LinkedTreeMap<String, Any>.toRace(): Race? {
    return get("race")?.let { gson.fromJson(gson.toJson(it), Race::class.java) }
  }
}
