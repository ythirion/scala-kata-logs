package onion.domain.model

import onion.adapter.persistence.ShoppingCartId

import scala.::

final case class ShoppingCart(val id: ShoppingCartId,
                              val orderedItems: Seq[OrderItem]) {
  def add(item: OrderItem): ShoppingCart = copy(orderedItems = item +: orderedItems)
}