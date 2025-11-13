package com.sedo.jwtauth.service

import com.sedo.jwtauth.event.OrderCompletedEvent
import com.sedo.jwtauth.model.dto.LoyaltyProgramDto
import com.sedo.jwtauth.model.entity.LoyaltyLevel
import com.sedo.jwtauth.model.entity.UserLoyalty
import com.sedo.jwtauth.repository.UserLoyaltyRepository
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class LoyaltyNotificationListener(private val userLoyaltyRepository: UserLoyaltyRepository) {

    private val logger = LoggerFactory.getLogger(LoyaltyNotificationListener::class.java)

    /**
     * listen to order completed events to add loyalty points
     */
    @EventListener
    @Async
    fun onOrderCompleted(event: OrderCompletedEvent) {
        addPointsForOrder(event.customerUserName, event.orderAmount)
        logger.info("✅ Points ajoutés pour commande ${event.orderId}")
    }

    /**
     * Add points based on order amount (1 point every 5 currency units)
     */
    private fun addPointsForOrder(customerUserName: String, orderAmount: BigDecimal) {
        val points = (orderAmount / BigDecimal(5)).toInt()
        if (points == 0) return

        val loyalty = userLoyaltyRepository.findByCustomerUserName(customerUserName)
            ?: UserLoyalty(customerUserName = customerUserName)

        val newPoints = loyalty.points + points
        val newLevel = LoyaltyLevel.fromPoints(newPoints)

        userLoyaltyRepository.save(
            loyalty.copy(
                points = newPoints,
                level = newLevel,
            )
        )
    }

    /**
     * get loyalty program details for a user
     */
    fun getUserLoyalty(customerUserName: String): LoyaltyProgramDto {
        val loyalty = userLoyaltyRepository.findByCustomerUserName(customerUserName)
            ?: UserLoyalty(customerUserName = customerUserName).let { userLoyaltyRepository.save(it) }

        val nextLevel = LoyaltyLevel.getNextLevel(loyalty.level)
        val nextLevelPoints = nextLevel?.minPoints ?: 0

        val progress = if (nextLevelPoints > 0) {
            (loyalty.points.toDouble() / nextLevelPoints * 100).coerceAtMost(100.0)
        } else 100.0

        return LoyaltyProgramDto(
            level = loyalty.level.displayName,
            points = loyalty.points,
            nextLevelPoints = nextLevelPoints,
            benefits = loyalty.level.benefits,
            progress = progress
        )
    }
}