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


@Service
class PayPalService() {
    val clientId = "AT_F-MUxVyzQ1S6SO230jcnyYHLiWhkkxDKWZvOdi0ldivuCzCmgvWJLvmzY5gA3LlPavkr_Kp9zBBSl"
    val clientSecret = "EAyX0fsJhns2j3aTyRY22a06LLiFvPYfqPcYwfvQlSLXfi6qD71neWFK9kkv1DniR6vhCWnEjEjXWuG7"
    val baseUrl = "https://api-m.sandbox.paypal.com"
    private val client = OkHttpClient()
    private val mapper = jacksonObjectMapper()
    val contentType = "application/json"

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