package onion.domain.model

import onion.adapter.persistence.ProductId
import onion.domain.service.ProductName

// Dependency on ProductId violates the architecture, since ProductId resides with persistence adapter
// Dependency on ProductName violates the architecture, since ProductName is located in the DomainService layer
final case class Product(val id: ProductId,
                         val name: ProductName)