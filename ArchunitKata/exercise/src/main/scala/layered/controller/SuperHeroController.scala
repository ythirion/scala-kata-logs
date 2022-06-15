package layered.controller

import layered.repository.SuperHeroRepository
import layered.service.SuperHeroService

class SuperHeroController(private val superHeroService: SuperHeroService,
                          private val superHeroRepository: SuperHeroRepository) {

}
