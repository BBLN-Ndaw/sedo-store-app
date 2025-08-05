package com.sedo.jwtauth.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import java.math.BigDecimal
import kotlin.reflect.KClass

/**
 * Custom validation annotation to ensure selling price is greater than purchase price
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PriceMarginValidator::class])
@MustBeDocumented
annotation class ValidPriceMargin(
    val message: String = "Selling price must be greater than purchase price",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

/**
 * Validator for PriceMargin constraint
 */
class PriceMarginValidator : ConstraintValidator<ValidPriceMargin, Any> {
    
    override fun isValid(value: Any?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true
        
        try {
            val clazz = value::class
            val purchasePriceField = clazz.java.getDeclaredField("purchasePrice")
            val sellingPriceField = clazz.java.getDeclaredField("sellingPrice")
            
            purchasePriceField.isAccessible = true
            sellingPriceField.isAccessible = true
            
            val purchasePrice = purchasePriceField.get(value) as? BigDecimal ?: return true
            val sellingPrice = sellingPriceField.get(value) as? BigDecimal ?: return true
            
            return sellingPrice > purchasePrice
        } catch (e: Exception) {
            return true // Si on ne peut pas valider, on laisse passer
        }
    }
}
