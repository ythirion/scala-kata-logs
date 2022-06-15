package onion.adapter.persistence

import java.util.UUID

final case class ProductId private (val id: UUID)

object ProductId {
  def from(id: UUID): Option[ProductId] = if (id != null) Some(ProductId(id)) else None
}