package com.sedo.jwtauth.controller

import com.sedo.jwtauth.mapper.toDto
import com.sedo.jwtauth.mapper.toEntity
import com.sedo.jwtauth.model.dto.OrderDto
import com.sedo.jwtauth.model.entity.Order
import com.sedo.jwtauth.model.entity.OrderStatus
import com.sedo.jwtauth.service.OrderService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService
) {
    
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun getAllOrders(): ResponseEntity<List<OrderDto>> {
        return ResponseEntity.ok(orderService.getAllOrders().map { it.toDto() })
    }
    @GetMapping("/customer")
    fun getOrdersByCustomer(): ResponseEntity<List<OrderDto>> {
        return ResponseEntity.ok(orderService.getOrdersByCustomerName().map { it.toDto() })
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun updateOrder(
        @PathVariable id: String,
        @RequestBody newOrder: OrderDto
    ): ResponseEntity<OrderDto> {
        return ResponseEntity.ok(orderService.updateOrder(id, newOrder.toEntity()).toDto())
    }

    @PutMapping("/cancel/{id}")
    fun cancelOrder(
        @PathVariable id: String,
    ): ResponseEntity<OrderDto> {
        return ResponseEntity.ok(orderService.cancelOrder(id).toDto())
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE', 'CLIENT')")
    fun getOrderById(@PathVariable id: String): ResponseEntity<OrderDto> {
        return ResponseEntity.ok(orderService.getOrderById(id).toDto())
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun getOrdersByStatus(@PathVariable status: OrderStatus): ResponseEntity<List<Order>> {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status))
    }
    

    
    @GetMapping("/pending")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun getPendingOrders(): ResponseEntity<List<OrderDto>> {
        return ResponseEntity.ok(orderService.getPendingOrders().map { it.toDto() })
    }
    
    @GetMapping("/ready-for-pickup")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun getReadyForPickupOrders(): ResponseEntity<List<OrderDto>> {
        return ResponseEntity.ok(orderService.getReadyForPickupOrders().map { it.toDto() })
    }
    
    @PostMapping
    fun createOrder(@Valid @RequestBody orderDto: OrderDto): ResponseEntity<Order> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(orderService.createOrder(orderDto.toEntity()))
    }
    

    
//    @PutMapping("/{id}/confirm")
//    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
//    fun confirmOrder(@PathVariable id: String): ResponseEntity<Order> {
//        return ResponseEntity.ok(orderService.updateOrder(id, OrderStatus.CONFIRMED))
//    }
    
//    @PutMapping("/{id}/prepare")
//    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
//    fun prepareOrder(@PathVariable id: String): ResponseEntity<Order> {
//        return ResponseEntity.ok(orderService.updateOrderStatus(id, OrderStatus.PREPARING))
//    }
    
//    @PutMapping("/{id}/ready")
//    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
//    fun markOrderReady(@PathVariable id: String): ResponseEntity<Order> {
//        return ResponseEntity.ok(orderService.updateOrder(id, OrderStatus.READY_FOR_PICKUP))
//    }
    
//    @PutMapping("/{id}/complete")
//    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
//    fun completeOrder(@PathVariable id: String): ResponseEntity<Order> {
//        return ResponseEntity.ok(orderService.updateOrderStatus(id, OrderStatus.COMPLETED))
//    }
    
//    @PutMapping("/{id}/cancel")
//    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
//    fun cancelOrder(
//        @PathVariable id: String,
//        @RequestBody cancelRequest: Map<String, String>
//    ): ResponseEntity<Order> {
//        val reason = cancelRequest["reason"] ?: "Cancelled by staff"
//        return ResponseEntity.ok(orderService.cancelOrder(id, reason))
//    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun getOrderStats(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(orderService.getOrderStats())
    }
}
