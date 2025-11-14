package com.sedo.jwtauth.eventListener

import com.sedo.jwtauth.event.OrderCompletedEvent
import com.sedo.jwtauth.repository.ProductRepository
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class StockNotificationListener(val productRepository: ProductRepository) {
    /**
     * Update product stock quantities when an order is completed
     */
    @EventListener
    fun onUpdateProductStock(event: OrderCompletedEvent) {
        event.orderItems.forEach { orderItem ->
            val product = productRepository.findById(orderItem.productId).orElse(null)
            product?.let {
                val updatedProduct = it.copy(
                        stockQuantity = it.stockQuantity - orderItem.quantity
                )
                productRepository.save(updatedProduct)
            }
        }
    }
}