package layered.repository

import scala.util.Try

trait SuperHeroRepository {
  def save(superHero: SuperHeroEntity): Try[SuperHeroEntity]
}