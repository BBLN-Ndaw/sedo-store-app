package com.sedo.jwtauth.mapper

import com.sedo.jwtauth.model.dto.OrderDto
import com.sedo.jwtauth.model.entity.Order

fun Order.toDto(): OrderDto =
    OrderDto(
        id = this.id,
        orderNumber = this.orderNumber,
        customerName = this.customerName,
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
        processedByUser = this.processedByUser,
        paymentMethod = this.paymentMethod,
        paymentStatus = this.paymentStatus,
        createdAt = this.createdAt,
    )

fun OrderDto.toEntity(): Order =
    Order(
        id = this.id,
        orderNumber = this.orderNumber ?: throw IllegalArgumentException("this number cannot be null"),
        customerName = this.customerName,
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
        processedByUser = this.processedByUser,
        paymentMethod = this.paymentMethod,
        paymentStatus = this.paymentStatus
    )