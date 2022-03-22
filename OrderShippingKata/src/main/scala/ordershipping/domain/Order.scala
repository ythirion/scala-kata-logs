package ordershipping.domain

import ordershipping.domain.OrderStatus.OrderStatus

import scala.collection.mutable

class Order(
    var total: Double,
    var currency: String,
    var items: mutable.MutableList[OrderItem],
    var tax: Double,
    var status: OrderStatus,
    var id: Int
)
