package layered.model

import java.util.UUID

case class SuperHero(val id: UUID,
                     val name: String,
                     val powers: Seq[String])