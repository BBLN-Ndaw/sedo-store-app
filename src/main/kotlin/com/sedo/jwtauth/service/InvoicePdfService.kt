package com.sedo.jwtauth.service

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import com.sedo.jwtauth.model.entity.Order
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Service class responsible for generating PDF invoices for completed orders.
 * 
 * This service provides comprehensive invoice generation functionality including:
 * - Professional PDF invoice creation using iText library
 * - Complete order information formatting
 * - Customer and billing address details
 * - Itemized product listings with pricing
 * - Tax calculations and totals
 * - French language formatting for business requirements
 * 
 * Business Logic:
 * - Generates invoices for confirmed and paid orders
 * - Includes complete order details, customer information, and itemization
 * - Calculates and displays taxes, shipping, and total amounts
 * - Formats dates and numbers according to French standards
 * - Provides professional invoice layout with store branding
 * 
 * Document Structure:
 * - Header with store name and invoice title
 * - Order and customer information section
 * - Billing and shipping address details
 * - Itemized product table with quantities and prices
 * - Summary section with subtotals, taxes, and total
 * - Footer with additional information
 * 
 * Integration Points:
 * - Order management system for order data
 * - Email service for invoice delivery
 * - Payment processing for confirmed orders
 * - Store configuration for branding
 * 
 * Dependencies:
 * - iText library for PDF generation
 * - Spring configuration for store information
 *
 */
@Service
class InvoicePdfService {

    private val logger = LoggerFactory.getLogger(InvoicePdfService::class.java)

    @Value("\${app.store.name:Magasin SEDO}")
    private lateinit var storeName: String

    /**
     * Generates a complete PDF invoice for an order.
     * 
     * This method creates a professional PDF invoice containing all order details,
     * customer information, itemized products, and financial summaries.
     * 
     * Document Generation Process:
     * 1. Creates PDF document with A4 page size
     * 2. Adds header with store name and invoice title
     * 3. Includes order information (number, date, customer, status)
     * 4. Adds billing and shipping address details
     * 5. Creates itemized table of ordered products
     * 6. Calculates and displays financial totals
     * 7. Adds footer with additional information
     * 
     * @param order The Order entity containing all order details
     * @param payerFullName The full name of the customer for billing
     * @return Byte array containing the generated PDF invoice
     * @throws RuntimeException if PDF generation fails
     */
    fun generateInvoicePdf(order: Order, payerFullName: String): ByteArray {
        logger.info("Generating PDF invoice for order: {}", order.orderNumber)

        val outputStream = ByteArrayOutputStream()
        val document = Document(PageSize.A4)

        try {
            PdfWriter.getInstance(document, outputStream)
            document.open()
            // En-tête du document
            addHeader(document)
            // Informations de la commande
            addOrderInfo(document, order, payerFullName)
            // Adresses
            addAddressInfo(document, order)
            // Articles
            addOrderItems(document, order)
            // Total
            addOrderTotal(document, order)
            // Pied de page
            addFooter(document)
            document.close()
            logger.info("PDF invoice generated successfully for order: {}", order.orderNumber)

        } catch (e: Exception) {
            logger.error("Error generating PDF invoice for order: {}", order.orderNumber, e)
            throw RuntimeException("Failed to generate PDF invoice", e)
        }

        return outputStream.toByteArray()
    }

    private fun addHeader(document: Document) {
        // Document title
        val title = Paragraph("FACTURE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24f, Color.DARK_GRAY))
        title.alignment = Element.ALIGN_CENTER
        title.spacingAfter = 20f
        document.add(title)
        // store name
        val storeTitle = Paragraph(storeName, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18f, Color.BLACK))
        storeTitle.alignment = Element.ALIGN_CENTER
        storeTitle.spacingAfter = 30f
        document.add(storeTitle)
    }

    private fun addOrderInfo(document: Document, order: Order, payerFullName: String) {
        val table = PdfPTable(2)
        table.widthPercentage = 100f
        table.setSpacingAfter(20f)
        // order number
        table.addCell(createInfoCell("Numéro de commande:", true))
        table.addCell(createInfoCell(order.orderNumber, false))
        // order date
        val orderDate = order.createdAt?.atZone(ZoneId.systemDefault())?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) ?: "N/A"
        table.addCell(createInfoCell("Date de commande:", true))
        table.addCell(createInfoCell(orderDate, false))

        // Client
        table.addCell(createInfoCell("Client:", true))
        table.addCell(createInfoCell(payerFullName, false))

        // Email
        table.addCell(createInfoCell("Email:", true))
        table.addCell(createInfoCell(order.customerEmail ?: "N/A", false))

        // Status
        table.addCell(createInfoCell("Statut:", true))
        table.addCell(createInfoCell(order.status.name, false))

        document.add(table)
    }

    private fun addAddressInfo(document: Document, order: Order) {
        val table = PdfPTable(2)
        table.widthPercentage = 100f
        table.setSpacingAfter(20f)

        // shipping address
        val shippingCell = PdfPCell()
        shippingCell.border = Rectangle.NO_BORDER
        shippingCell.addElement(Paragraph("ADRESSE DE LIVRAISON", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f)))
        order.shippingAddress?.let { address ->
            shippingCell.addElement(Paragraph(address.street, FontFactory.getFont(FontFactory.HELVETICA, 10f)))
            shippingCell.addElement(Paragraph("${address.postalCode} ${address.city}", FontFactory.getFont(FontFactory.HELVETICA, 10f)))
            shippingCell.addElement(Paragraph(address.country, FontFactory.getFont(FontFactory.HELVETICA, 10f)))
        }

        // Billing address
        val billingCell = PdfPCell()
        billingCell.border = Rectangle.NO_BORDER
        billingCell.addElement(Paragraph("ADRESSE DE FACTURATION", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f)))
        order.billingAddress?.let { address ->
            billingCell.addElement(Paragraph(address.street, FontFactory.getFont(FontFactory.HELVETICA, 10f)))
            billingCell.addElement(Paragraph("${address.postalCode} ${address.city}", FontFactory.getFont(FontFactory.HELVETICA, 10f)))
            billingCell.addElement(Paragraph(address.country, FontFactory.getFont(FontFactory.HELVETICA, 10f)))
        }

        table.addCell(shippingCell)
        table.addCell(billingCell)
        document.add(table)
    }

    private fun addOrderItems(document: Document, order: Order) {
        val table = PdfPTable(5)
        table.widthPercentage = 100f
        table.setSpacingAfter(20f)
        table.setWidths(floatArrayOf(3f, 1f, 1.5f, 1f, 1.5f))

        // Headers
        table.addCell(createHeaderCell("Article"))
        table.addCell(createHeaderCell("Qté"))
        table.addCell(createHeaderCell("Prix HT"))
        table.addCell(createHeaderCell("TVA"))
        table.addCell(createHeaderCell("Total HT"))

        // Items
        order.items.forEach { item ->
            table.addCell(createItemCell(item.productName))
            table.addCell(createItemCell(item.quantity.toString()))
            table.addCell(createItemCell("${item.productUnitPrice}€"))
            table.addCell(createItemCell("${(item.productTaxRate * BigDecimal(100)).setScale(2, RoundingMode.DOWN)}%"))
            table.addCell(createItemCell("${(item.productUnitPrice * BigDecimal(item.quantity)).setScale(2, RoundingMode.DOWN)}€"))
        }

        document.add(table)
    }

    private fun addOrderTotal(document: Document, order: Order) {
        val table = PdfPTable(2)
        table.widthPercentage = 60f
        table.horizontalAlignment = Element.ALIGN_RIGHT
        table.setSpacingAfter(20f)

        // Sub-total HT
        table.addCell(createTotalLabelCell("Sous-total HT:"))
        table.addCell(createTotalValueCell("${order.subtotal}€"))

        // TVA
        table.addCell(createTotalLabelCell("TVA:"))
        table.addCell(createTotalValueCell("${order.taxAmount}€"))

        // Shipping cost
        table.addCell(createTotalLabelCell("Frais de port:"))
        table.addCell(createTotalValueCell("${order.shippingAmount}€"))

        // Total
        val totalCell = createTotalLabelCell("TOTAL TTC:")
        totalCell.backgroundColor = Color.LIGHT_GRAY
        table.addCell(totalCell)

        val totalValueCell = createTotalValueCell("${order.totalAmount}€")
        totalValueCell.backgroundColor = Color.LIGHT_GRAY
        table.addCell(totalValueCell)

        document.add(table)
    }

    private fun addFooter(document: Document) {
        val footer = Paragraph("Merci pour votre commande !", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12f, Color.GRAY))
        footer.alignment = Element.ALIGN_CENTER
        footer.spacingBefore = 30f
        document.add(footer)

        val contact = Paragraph("Pour toute question, contactez-nous à sedosebe.store@gmail.com",
            FontFactory.getFont(FontFactory.HELVETICA, 10f, Color.GRAY))
        contact.alignment = Element.ALIGN_CENTER
        document.add(contact)
    }

    private fun createInfoCell(text: String, isBold: Boolean): PdfPCell {
        val font = if (isBold) FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f)
                   else FontFactory.getFont(FontFactory.HELVETICA, 10f)
        val cell = PdfPCell(Phrase(text, font))
        cell.border = Rectangle.NO_BORDER
        cell.paddingBottom = 5f
        return cell
    }

    private fun createHeaderCell(text: String): PdfPCell {
        val cell = PdfPCell(Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f, Color.WHITE)))
        cell.backgroundColor = Color.DARK_GRAY
        cell.horizontalAlignment = Element.ALIGN_CENTER
        cell.paddingTop = 8f
        cell.paddingBottom = 8f
        return cell
    }

    private fun createItemCell(text: String): PdfPCell {
        val cell = PdfPCell(Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 9f)))
        cell.paddingTop = 5f
        cell.paddingBottom = 5f
        return cell
    }

    private fun createTotalLabelCell(text: String): PdfPCell {
        val cell = PdfPCell(Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f)))
        cell.horizontalAlignment = Element.ALIGN_RIGHT
        cell.border = Rectangle.NO_BORDER
        cell.paddingTop = 5f
        cell.paddingBottom = 5f
        return cell
    }

    private fun createTotalValueCell(text: String): PdfPCell {
        val cell = PdfPCell(Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f)))
        cell.horizontalAlignment = Element.ALIGN_RIGHT
        cell.border = Rectangle.NO_BORDER
        cell.paddingTop = 5f
        cell.paddingBottom = 5f
        return cell
    }
}
