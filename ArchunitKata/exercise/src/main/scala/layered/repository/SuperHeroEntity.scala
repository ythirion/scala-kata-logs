package layered.repository

import java.util.UUID

case class SuperHeroEntity(val id: UUID,
                          val name: String,
                          val powers: Seq[String])