package com.sedo.jwtauth.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sedo.jwtauth.model.dto.PaypalCapturedResponse
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Service

/**
 * Service class responsible for PayPal payment integration in the Store Management System.
 * 
 * This service provides comprehensive PayPal payment processing functionality including:
 * - PayPal OAuth token management
 * - Order creation in PayPal sandbox environment
 * - Payment capture and confirmation
 * - Integration with PayPal REST API v2
 * - Error handling and response processing
 * 
 * Business Logic:
 * - Uses PayPal sandbox environment for development/testing
 * - Supports EUR currency for European market
 * - Creates payment orders with invoice correlation
 * - Captures payments after user approval
 * - Returns structured payment confirmation data
 * 
 * Payment Flow:
 * 1. Authenticate with PayPal using client credentials
 * 2. Create payment order with amount and invoice ID
 * 3. Customer completes payment on PayPal
 * 4. Capture payment to finalize transaction
 * 5. Return payment confirmation details
 * 
 * Security:
 * - Uses OAuth 2.0 client credentials flow
 * - Secure API communication over HTTPS
 * - Sandbox environment for safe testing
 * - Token-based authentication for all API calls
 * 
 * Integration Points:
 * - Order management system for payment correlation
 * - Frontend payment interface
 * - Order confirmation and fulfillment processes
 * 
 * Dependencies:
 * - OkHttp for HTTP client communication
 * - Jackson for JSON parsing and serialization
 * - PayPal REST API v2
 *
 */
@Service
class PayPalService() {
    val clientId = "AT_F-MUxVyzQ1S6SO230jcnyYHLiWhkkxDKWZvOdi0ldivuCzCmgvWJLvmzY5gA3LlPavkr_Kp9zBBSl"
    val clientSecret = "EAyX0fsJhns2j3aTyRY22a06LLiFvPYfqPcYwfvQlSLXfi6qD71neWFK9kkv1DniR6vhCWnEjEjXWuG7"
    val baseUrl = "https://api-m.sandbox.paypal.com"
    private val client = OkHttpClient()
    private val mapper = jacksonObjectMapper()
    val contentType = "application/json"

    /**
     * Obtains an access token from PayPal for API authentication.
     * 
     * Uses OAuth 2.0 client credentials flow to authenticate with PayPal.
     * The token is required for all subsequent PayPal API calls.
     * 
     * @return PayPal access token for API authentication
     * @throws RuntimeException if token acquisition fails
     */
    fun getAccessToken(): String {
        val credential = okhttp3.Credentials.basic(clientId, clientSecret)
        val request = Request.Builder()
            .url("$baseUrl/v1/oauth2/token?grant_type=client_credentials")
            .post(FormBody.Builder().add("grant_type", "client_credentials").build())
            .header("Authorization", credential)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw RuntimeException("Erreur PayPal: ${response.body?.string()}")
            val json = mapper.readTree(response.body!!.string())
            return json.get("access_token").asText()
        }
    }

    /**
     * Creates a new payment order in PayPal system.
     * 
     * This method initiates the payment process by creating an order in PayPal
     * with the specified amount and correlating it with the internal order number.
     * 
     * Order Creation Process:
     * 1. Authenticates with PayPal to get access token
     * 2. Creates payment order with EUR currency
     * 3. Associates order with internal invoice ID
     * 4. Returns PayPal order ID for payment flow
     * 
     * @param amount The payment amount in EUR (as string)
     * @param orderNumber The internal order number for correlation
     * @return PayPal order ID for payment processing
     * @throws RuntimeException if order creation fails
     */
    fun createOrder(amount: String, orderNumber: String ): String {
        val token = getAccessToken()
        val body = """
        {
          "intent": "CAPTURE",
          "purchase_units": [{
            "invoice_id": "$orderNumber",
            "amount": {
              "currency_code": "EUR",
              "value": "$amount"
            }
          }]
        }
        """.trimIndent().toRequestBody(contentType.toMediaType())

        val request = Request.Builder()
            .url("$baseUrl/v2/checkout/orders")
            .post(body)
            .header("Authorization", "Bearer $token")
            .header("Content-Type", contentType)
            .build()

        client.newCall(request).execute().use { response ->
            val responseBodyString = response.body?.string()
            if (!response.isSuccessful) throw RuntimeException("Error Creating PayPal payment: $responseBodyString")
            val json = mapper.readTree(responseBodyString)
            return json.get("id").asText()
        }
    }

    /**
     * Captures a PayPal order to finalize the payment.
     * 
     * This method is called after the customer has approved the payment
     * on PayPal's interface. It finalizes the transaction and captures the funds.
     * 
     * Payment Capture Process:
     * 1. Authenticates with PayPal using access token
     * 2. Sends capture request for the approved order
     * 3. Processes PayPal's response with payment details
     * 4. Returns structured payment confirmation data
     * 
     * @param orderId The PayPal order ID to capture
     * @return PaypalCapturedResponse containing payment confirmation details
     * @throws RuntimeException if payment capture fails
     */
    fun captureOrder(orderId: String): PaypalCapturedResponse {
        val token = getAccessToken()
        val body = "".toRequestBody(contentType.toMediaType())
        val request = Request.Builder()
            .url("$baseUrl/v2/checkout/orders/$orderId/capture")
            .post(body)
            .header("Authorization", "Bearer $token")
            .header("Content-Type", contentType)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw RuntimeException("Error capturing Payment: ${response.body?.string()}")
            val body = response.body?.string() ?: throw RuntimeException("Empty body while creating payment")
            val paypalResponse: PaypalCapturedResponse = mapper.readValue(body)
            return paypalResponse
        }
    }

}