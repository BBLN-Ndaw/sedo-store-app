package com.sedo.jwtauth.service

import com.sedo.jwtauth.event.OrderCompletedEvent
import com.sedo.jwtauth.model.dto.LoyaltyProgramDto
import com.sedo.jwtauth.model.entity.LoyaltyLevel
import com.sedo.jwtauth.model.entity.UserLoyalty
import com.sedo.jwtauth.repository.UserLoyaltyRepository
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class LoyaltyService(private val userLoyaltyRepository: UserLoyaltyRepository) {

    private val logger = LoggerFactory.getLogger(LoyaltyService::class.java)

    /**
     * Écoute les commandes terminées
     */
    @EventListener
    fun onOrderCompleted(event: OrderCompletedEvent) {
        addPointsForOrder(event.customerUserName, event.orderAmount)
        logger.info("✅ Points ajoutés pour commande ${event.orderId}")
    }

    /**
     * Ajoute des points : 1 point pour 5€
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
     * Récupère les infos de fidélité
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