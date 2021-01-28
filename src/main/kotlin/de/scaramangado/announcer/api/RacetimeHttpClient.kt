package de.scaramangado.announcer.api

import de.scaramangado.announcer.api.model.Category
import de.scaramangado.announcer.api.model.Race
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.getForEntity

@Service
class RacetimeHttpClient(private val properties: RacetimeProperties,
                         private val restTemplateBuilder: RestTemplateBuilder) {

  fun getRacesOfCategory(categorySlug: String): List<Race> {
    val category =
        restTemplateBuilder.build()
            .getForEntity<Category>("${properties.baseUrl}/$categorySlug/data")

    return category.body?.currentRaces ?: emptyList()
  }
}
