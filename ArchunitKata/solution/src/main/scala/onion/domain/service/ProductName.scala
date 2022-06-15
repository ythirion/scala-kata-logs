package onion.domain.service

final case class ProductName private (val name: String)

object ProductName {
  def from(name: String): Option[ProductName] = if (!name.isEmpty) Some(ProductName(name)) else None
}