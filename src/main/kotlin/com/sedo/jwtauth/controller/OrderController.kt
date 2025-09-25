package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Roles.ADMIN_ROLE
import com.sedo.jwtauth.constants.Constants.Roles.EMPLOYEE_ROLE
import com.sedo.jwtauth.mapper.toDto
import com.sedo.jwtauth.model.dto.CartDto
import com.sedo.jwtauth.model.dto.OrderDto
import com.sedo.jwtauth.model.dto.PaypalCapturedResponse
import com.sedo.jwtauth.model.entity.Order
import com.sedo.jwtauth.model.entity.OrderStatus
import com.sedo.jwtauth.service.OrderService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService
) {
    
    @GetMapping
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getAllOrders(): ResponseEntity<List<OrderDto>> {
        return ResponseEntity.ok(orderService.getAllOrders().map { it.toDto() })
    }
    @GetMapping("/customer")
    fun getOrdersByCustomerUserName(): ResponseEntity<List<OrderDto>> {
        return ResponseEntity.ok(orderService.getOrdersByCustomerUserName().map { it.toDto() })
    }

    @GetMapping("/customer/{customerUserName}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getCustomerOrder(@PathVariable customerUserName: String): ResponseEntity<List<OrderDto>> {
        return ResponseEntity.ok(orderService.getCustomerOrders(customerUserName).map { it.toDto() })
    }

    @PostMapping("/create")
    fun createOrder(@Valid @RequestBody cart: CartDto): ResponseEntity<Order> {
        return ResponseEntity.status(CREATED)
            .body(orderService.createOrder(cart))
    }

    @PostMapping("/capture-order")
    fun captureOrder(@RequestParam orderId: String): ResponseEntity<PaypalCapturedResponse> {
        return ResponseEntity.ok(orderService.captureOrder(orderId))
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateOrderStatus(
        @PathVariable id: String,
        @RequestBody newOrderStatus: OrderStatus
    ): ResponseEntity<OrderDto> {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, newOrderStatus).toDto())
    }

    @PutMapping("/cancel/{id}")
    fun cancelOrder(
        @PathVariable id: String,
    ): ResponseEntity<OrderDto> {
        return ResponseEntity.ok(orderService.cancelOrder(id).toDto())
    }
    
    @GetMapping("/{id}")
    fun getOrderById(@PathVariable id: String): ResponseEntity<OrderDto> {
        return ResponseEntity.ok(orderService.getOrderById(id).toDto())
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
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
    
    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun getOrderStats(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(orderService.getOrderStats())
    }
}
