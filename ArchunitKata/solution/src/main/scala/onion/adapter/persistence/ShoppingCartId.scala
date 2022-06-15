package onion.adapter.persistence

import java.util.UUID

final case class ShoppingCartId private(val id: UUID)

object ShoppingCartId {
  def from(id: UUID): Option[ShoppingCartId] = if (id != null) Some(ShoppingCartId(id)) else None
}