package onion.adapter.rest

import onion.adapter.persistence.{ProductId, ShoppingCartId}
import onion.application.ShoppingService
import onion.domain.model.ShoppingCart

class ShoppingController(private val shoppingService: ShoppingService) {
  def addToCart(cartId: ShoppingCartId,
                productId: ProductId,
                quantity: Int): ShoppingCart = shoppingService.addToCart(cartId, productId, quantity)
}