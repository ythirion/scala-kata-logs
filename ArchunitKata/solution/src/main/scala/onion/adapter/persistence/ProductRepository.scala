package onion.adapter.persistence

trait ProductRepository {
  def getById(id: ProductId): onion.domain.model.Product
}
