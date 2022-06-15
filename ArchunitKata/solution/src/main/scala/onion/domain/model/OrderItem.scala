package onion.domain.model

final case class OrderItem private (val product: Product, val quantity: Int)

object OrderItem {
  def from(product: Product, quantity: Int): Option[OrderItem] =
    if(quantity > 0) Some(OrderItem(product, quantity)) else None
}