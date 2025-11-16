package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Roles.ADMIN_ROLE
import com.sedo.jwtauth.constants.Constants.Roles.EMPLOYEE_ROLE
import com.sedo.jwtauth.mapper.toDto
import com.sedo.jwtauth.model.dto.CartDto
import com.sedo.jwtauth.model.dto.DailySalesResponseDto
import com.sedo.jwtauth.model.dto.DailySalesRequestDto
import com.sedo.jwtauth.model.dto.OrderDto
import com.sedo.jwtauth.model.dto.OrderStatusUpdateRequest
import com.sedo.jwtauth.model.dto.PaypalCapturedResponse
import com.sedo.jwtauth.model.dto.TopSellingProductDto
import com.sedo.jwtauth.model.entity.Order
import com.sedo.jwtauth.service.OrderService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
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
    fun getAllOrders(@RequestParam(defaultValue = "0") page: Int,
                     @RequestParam(defaultValue = "50") size: Int): ResponseEntity<Page<OrderDto>> {
        return ResponseEntity.ok(orderService.getAllOrders(page, size).map { it.toDto() })
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun searchOrders(@RequestParam(required = false) search: String?,
                     @RequestParam(required = false) status: String?,
                     @RequestParam(required = false) period: String?,
                     @RequestParam(defaultValue = "0") page: Int,
                     @RequestParam(defaultValue = "50") size: Int): ResponseEntity<Page<OrderDto>> {
        return ResponseEntity.ok(orderService.searchOrders(search, status, period, page, size).map { it.toDto() })
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

    @PatchMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateOrderStatus(
        @PathVariable id: String,
        @RequestBody newOrderStatus: OrderStatusUpdateRequest
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
    
    @GetMapping("/analytics/top-selling")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getTopSellingProducts(@RequestParam(defaultValue = "5") limit: Int): ResponseEntity<List<TopSellingProductDto>> {
        return ResponseEntity.ok(orderService.getTopSellingProducts(limit))
    }

    @GetMapping("/analytics/daily-selling")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getDailySalesSummary(@RequestParam dailySalesRequestDto: DailySalesRequestDto): ResponseEntity<DailySalesResponseDto> {
        return ResponseEntity.ok(orderService.getDailySalesSummary(dailySalesRequestDto))
    }
}
