package onion.application

import onion.adapter.persistence.{ProductId, ProductRepository, ShoppingCartId, ShoppingCartRepository}
import onion.domain.model.{OrderItem, ShoppingCart}

class ShoppingService(val shoppingCartRepository: ShoppingCartRepository,
                      val productRepository: ProductRepository) {
  def addToCart(cartId: ShoppingCartId,
                productId: ProductId,
                quantity: Int): ShoppingCart = {
    val shoppingCart = shoppingCartRepository.getById(cartId)
    val product = productRepository.getById(productId)

    OrderItem.from(product, quantity)
      .map(shoppingCart.add(_))
      .map(shoppingCartRepository.save(_))
      .getOrElse(throw new Exception("Je balance une exception dégueu au lieu de traiter mes cas d'erreurs"))
  }
}
