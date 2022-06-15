package onion.adapter.persistence

import onion.domain.model.ShoppingCart

trait ShoppingCartRepository {
  def getById(id: ShoppingCartId): ShoppingCart
  def save(shoppingCart: ShoppingCart): ShoppingCart
}
