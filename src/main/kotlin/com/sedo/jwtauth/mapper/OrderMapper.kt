package com.sedo.jwtauth.mapper

import com.sedo.jwtauth.model.dto.CartItemDto
import com.sedo.jwtauth.model.dto.OrderDto
import com.sedo.jwtauth.model.entity.Order
import com.sedo.jwtauth.model.entity.OrderItem

fun Order.toDto(): OrderDto =
    OrderDto(
        id = this.id,
        orderNumber = this.orderNumber,
        customerName = this.customerUserName,
        customerEmail = this.customerEmail,
        customerNumTel = this.customerNumTel,
        status = this.status,
        totalAmount = this.totalAmount,
        subtotal = this.subtotal,
        shippingAmount = this.shippingAmount,
        taxAmount = this.taxAmount,
        shippingAddress = this.shippingAddress,
        billingAddress = this.billingAddress,
        estimatedDeliveryDate = this.estimatedDeliveryDate,
        notes = this.notes,
        items = this.items,
        pickupDate = this.pickupDate,
        paymentOrderId = this.paymentOrderId,
        processedByUser = this.processedByUser,
        paymentMethod = this.paymentMethod,
        paymentStatus = this.paymentStatus,
        createdAt = this.createdAt,
    )

fun CartItemDto.toOrderItem() = OrderItem(
    productId = this.productId,
    productName = this.productName,
    image = this.imageUrl,
    quantity = this.quantity,
    productUnitPrice = this.productUnitPriceHT
)