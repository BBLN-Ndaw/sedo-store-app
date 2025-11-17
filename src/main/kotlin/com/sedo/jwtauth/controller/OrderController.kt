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

/**
 * REST Controller for comprehensive order management and e-commerce operations.
 *
 * This controller serves as the central hub for all order-related operations in the
 * store management system, including order creation, status tracking, payment processing,
 * customer order history, and business analytics. It supports both customer-facing
 * operations and administrative order management workflows.
 *
 * Key functionalities include:
 * - Order lifecycle management (creation, updates, cancellation)
 * - Payment processing integration with PayPal
 * - Customer order tracking and history
 * - Administrative order search and filtering
 * - Sales analytics and reporting
 *
 * @property orderService Service layer for order business logic and operations
 *
 */
@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService
) {
    
    /**
     * Retrieves all orders in the system with pagination support.
     *
     * This administrative endpoint provides access to all orders for management
     * and oversight purposes. Results are paginated for performance optimization
     * and include comprehensive order details.
     *
     * @param page Page number for pagination (zero-based, default: 0)
     * @param size Number of orders per page (default: 50, max: 100)
     * @return ResponseEntity containing paginated OrderDto objects
     *
     * Security: Requires ADMIN or EMPLOYEE role for access to all order data
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getAllOrders(@RequestParam(defaultValue = "0") page: Int,
                     @RequestParam(defaultValue = "50") size: Int): ResponseEntity<Page<OrderDto>> {
        return ResponseEntity.ok(orderService.getAllOrders(page, size).map { it.toDto() })
    }

    /**
     * Searches and filters orders based on multiple criteria.
     *
     * This endpoint provides advanced search functionality for orders,
     * supporting text search, status filtering, and time period constraints.
     * Essential for order management and customer service operations.
     *
     * @param search Optional text search query (order ID, customer name, product names)
     * @param status Optional order status filter (PENDING, COMPLETED, CANCELLED, etc.)
     * @param period Optional time period filter (TODAY, WEEK, MONTH, CUSTOM)
     * @param page Page number for pagination (zero-based, default: 0)
     * @param size Number of orders per page (default: 50)
     * @return ResponseEntity containing paginated filtered OrderDto objects
     *
     * Security: Requires ADMIN or EMPLOYEE role for order search operations
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun searchOrders(@RequestParam(required = false) search: String?,
                     @RequestParam(required = false) status: String?,
                     @RequestParam(required = false) period: String?,
                     @RequestParam(defaultValue = "0") page: Int,
                     @RequestParam(defaultValue = "50") size: Int): ResponseEntity<Page<OrderDto>> {
        return ResponseEntity.ok(orderService.searchOrders(search, status, period, page, size).map { it.toDto() })
    }

    /**
     * Retrieves all orders for the currently authenticated customer.
     *
     * @return ResponseEntity containing list of customer's OrderDto objects
     *
     * Security: Accessible to authenticated customers for their own order history
     */
    @GetMapping("/customer")
    fun getOrdersByCustomerUserName(): ResponseEntity<List<OrderDto>> {
        return ResponseEntity.ok(orderService.getOrdersByCustomerUserName().map { it.toDto() })
    }

    /**
     * Retrieves all orders for a specific customer (administrative access).
     *
     * @param customerUserName Username of the customer whose orders to retrieve
     * @return ResponseEntity containing list of customer's OrderDto objects
     * @throws CustomerNotFoundException if customer doesn't exist
     *
     * Security: Requires ADMIN or EMPLOYEE role for accessing customer order data
     */
    @GetMapping("/customer/{customerUserName}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getCustomerOrder(@PathVariable customerUserName: String): ResponseEntity<List<OrderDto>> {
        return ResponseEntity.ok(orderService.getCustomerOrders(customerUserName).map { it.toDto() })
    }

    /**
     * Creates a new order from customer's shopping cart.
     *
     * This endpoint processes cart items, validates inventory, calculates totals,
     * and creates a new order ready for payment processing.
     *
     * @param cart Valid CartDto containing items, quantities, and delivery information
     * @return ResponseEntity with HTTP 201 status containing created Order entity
     * @throws InsufficientInventoryException if requested quantities exceed stock
     * @throws ValidationException if cart data is invalid
     *
     * Security: Accessible to authenticated customers
     */
    @PostMapping("/create")
    fun createOrder(@Valid @RequestBody cart: CartDto): ResponseEntity<Order> {
        return ResponseEntity.status(CREATED)
            .body(orderService.createOrder(cart))
    }

    /**
     * Captures payment for an existing order through PayPal integration.
     *
     * This endpoint finalizes payment processing and updates order status
     * upon successful payment capture from PayPal.
     *
     * @param orderId Unique identifier of the order to capture payment for
     * @return ResponseEntity containing PayPal capture response details
     * @throws OrderNotFoundException if order doesn't exist
     * @throws PaymentProcessingException if payment capture fails
     *
     * Security: Accessible to authenticated customers for their own orders
     */
    @PostMapping("/capture-order")
    fun captureOrder(@RequestParam orderId: String): ResponseEntity<PaypalCapturedResponse> {
        return ResponseEntity.ok(orderService.captureOrder(orderId))
    }

    /**
     * Updates the status of an existing order.
     *
     * This administrative endpoint allows staff to change order status
     * for order processing workflow (e.g., PENDING → PROCESSING → SHIPPED).
     *
     * @param id Unique identifier of the order to update
     * @param newOrderStatus Request containing new status and optional notes
     * @return ResponseEntity containing updated OrderDto
     * @throws OrderNotFoundException if order doesn't exist
     * @throws InvalidStatusTransitionException if status change is not allowed
     *
     * Security: Requires ADMIN or EMPLOYEE role for order status management
     */
    @PatchMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateOrderStatus(
        @PathVariable id: String,
        @RequestBody newOrderStatus: OrderStatusUpdateRequest
    ): ResponseEntity<OrderDto> {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, newOrderStatus).toDto())
    }

    /**
     * Cancels an existing order.
     *
     * This endpoint allows customers to cancel their orders (if eligible)
     * and handles inventory restoration and refund processing.
     *
     * @param id Unique identifier of the order to cancel
     * @return ResponseEntity containing cancelled OrderDto
     * @throws OrderNotFoundException if order doesn't exist
     * @throws OrderCancellationException if order cannot be cancelled
     *
     * Security: Accessible to customers for their own orders, or ADMIN/EMPLOYEE for any order
     */
    @PutMapping("/cancel/{id}")
    fun cancelOrder(
        @PathVariable id: String,
    ): ResponseEntity<OrderDto> {
        return ResponseEntity.ok(orderService.cancelOrder(id).toDto())
    }
    
    /**
     * Retrieves detailed information for a specific order.
     *
     * @param id Unique identifier of the order to retrieve
     * @return ResponseEntity containing detailed OrderDto
     * @throws OrderNotFoundException if order doesn't exist
     *
     * Security: Customers can access their own orders, ADMIN/EMPLOYEE can access any order
     */
    @GetMapping("/{id}")
    fun getOrderById(@PathVariable id: String): ResponseEntity<OrderDto> {
        return ResponseEntity.ok(orderService.getOrderById(id).toDto())
    }
    
    /**
     * Retrieves analytics data for top-selling products.
     *
     * This endpoint provides business intelligence on product performance,
     * helping with inventory management and marketing decisions.
     *
     * @param limit Maximum number of top products to return (default: 5)
     * @return ResponseEntity containing list of TopSellingProductDto objects
     *
     * Security: Requires ADMIN or EMPLOYEE role for access to sales analytics
     */
    @GetMapping("/analytics/top-selling")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getTopSellingProducts(@RequestParam(defaultValue = "5") limit: Int): ResponseEntity<List<TopSellingProductDto>> {
        return ResponseEntity.ok(orderService.getTopSellingProducts(limit))
    }

    /**
     * Retrieves daily sales summary analytics for specified time periods.
     *
     * This endpoint provides detailed sales performance data including
     * revenue, order counts, and trend analysis for business reporting.
     *
     * @param dailySalesRequestDto Request parameters specifying date range and filters
     * @return ResponseEntity containing DailySalesResponseDto with sales analytics
     *
     * Security: Requires ADMIN or EMPLOYEE role for access to sales data
     */
    @GetMapping("/analytics/daily-selling")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getDailySalesSummary(@RequestParam dailySalesRequestDto: DailySalesRequestDto): ResponseEntity<DailySalesResponseDto> {
        return ResponseEntity.ok(orderService.getDailySalesSummary(dailySalesRequestDto))
    }
}
