package com.sedo.jwtauth.model.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PaypalCapturedResponse(
    val id: String,
    val status: String,
    val payment_source: PaymentSource,
    val purchase_units: List<PurchaseUnit>,
    val payer: Payer
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PaymentSource(
    val paypal: PaypalAccount
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PaypalAccount(
    val email_address: String,
    val name: Name,
    val address: PaypalCustomerAddress
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Name(
    val given_name: String? = null,
    val surname: String? = null,
    val full_name: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PaypalCustomerAddress(
    val address_line_1: String? = null,
    val admin_area_2: String? = null,
    val postal_code: String? = null,
    val country_code: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PurchaseUnit(
    val shipping: Shipping,
    val payments: Payments
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Shipping(
    val name: Name,
    val address: PaypalCustomerAddress
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Payments(
    val captures: List<Capture>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Capture(
    val amount: Amount
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Amount(
    val currency_code: String,
    val value: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Payer(
    val name: Name,
    val email_address: String,
    val address: PaypalCustomerAddress
)
