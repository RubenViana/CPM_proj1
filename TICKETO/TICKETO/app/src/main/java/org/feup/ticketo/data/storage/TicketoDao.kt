package org.feup.ticketo.data.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface TicketoDao {

    // Get all customer tickets for an event
    @Query("SELECT * FROM TICKET, EVENT WHERE TICKET.EVENT_ID = EVENT.EVENT_ID AND EVENT.EVENT_ID = :eventId AND PURCHASE_ID IN (SELECT PURCHASE_ID FROM PURCHASE WHERE CUSTOMER_ID = :customerId)")
    suspend fun getCustomerTicketsForEvent(customerId: String, eventId: Int): EventTickets?

    // Get unused customer tickets for an event
    @Query("SELECT * FROM TICKET, EVENT WHERE USED = 0 AND TICKET.EVENT_ID = EVENT.EVENT_ID AND EVENT.EVENT_ID = :eventId AND PURCHASE_ID IN (SELECT PURCHASE_ID FROM PURCHASE WHERE CUSTOMER_ID = :customerId) ")
    suspend fun getUnusedCustomerTicketsForEvent(customerId: String, eventId: Int): EventTickets?

    // Get used customer tickets for an event
    @Query("SELECT * FROM TICKET, EVENT WHERE TICKET.EVENT_ID = EVENT.EVENT_ID AND EVENT.EVENT_ID = :eventId AND PURCHASE_ID IN (SELECT PURCHASE_ID FROM PURCHASE WHERE CUSTOMER_ID = :customerId) AND TICKET.USED = true")
    suspend fun getUsedCustomerTicketsForEvent(customerId: String, eventId: Int): EventTickets?

    // Get customer tickets for an event
    @Query("SELECT * FROM EVENT WHERE EVENT_ID = :eventId")
    suspend fun getEvent(eventId: Int): Event?

    // Get all future events
    @Query("SELECT * FROM EVENT WHERE DATE >= :date")
    suspend fun getAllFutureEvents(date: String): List<Event>?

    // Get events for which a customer has purchased tickets along with the count of tickets bought for each event
    @Query(
        """
        SELECT e.*, COUNT(t.TICKET_ID) AS tickets_count
        FROM EVENT e 
        INNER JOIN TICKET t ON e.EVENT_ID = t.EVENT_ID 
        INNER JOIN PURCHASE p ON t.PURCHASE_ID = p.PURCHASE_ID 
        WHERE p.CUSTOMER_ID = :customerId 
        GROUP BY e.EVENT_ID
    """
    )
    suspend fun getEventsWithTicketCount(customerId: String): List<EventWithTicketsCount>?

    @Insert
    suspend fun insertCustomer(customer: Customer)

    // Insert a credit card into the database
    @Insert
    suspend fun insertCreditCard(creditCard: CreditCard)

    // Insert an event into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    // Insert an order into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    // Insert a product into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    // Insert an order product into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderProduct(orderProduct: OrderProduct)

    // Insert a purchase into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: Purchase)

    // Insert a ticket into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: Ticket)

    // Insert a voucher into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoucher(voucher: Voucher)

    // Delete all purchases, tickets, and events associated with a client
    @Query("DELETE FROM PURCHASE WHERE CUSTOMER_ID = :customerId")
    suspend fun deletePurchasesForClient(customerId: String)

    // Delete all tickets associated with a specific purchase
    @Query("DELETE FROM TICKET WHERE PURCHASE_ID IN (SELECT PURCHASE_ID FROM PURCHASE WHERE CUSTOMER_ID = :customerId)")
    suspend fun deleteTicketsForClient(customerId: String)

    // Delete all events associated with tickets purchased by a client
    @Query("DELETE FROM EVENT WHERE EVENT_ID IN (SELECT EVENT_ID FROM TICKET WHERE PURCHASE_ID IN (SELECT PURCHASE_ID FROM PURCHASE WHERE CUSTOMER_ID = :customerId))")
    suspend fun deleteEventsForClient(customerId: String)

    // Get all purchases along with their associated tickets and events for a client
    @Transaction
    @Query("SELECT * FROM PURCHASE WHERE CUSTOMER_ID = :customerId")
    suspend fun getPurchasesWithTicketsAndEventsForClient(customerId: String): List<PurchaseWithTicketsAndEvents>

    // Get all vouchers for a specific customer
    @Query("SELECT * FROM VOUCHER WHERE CUSTOMER_ID = :customerId AND ORDER_ID IS NULL")
    suspend fun getUnusedVouchersForCustomer(customerId: String): List<Voucher>

    // Get all products
    @Query("SELECT * FROM PRODUCT")
    suspend fun getAllProducts(): List<Product>

    // Get one product
    @Query("SELECT * FROM PRODUCT WHERE PRODUCT_ID = :productId")
    fun getProduct(productId: Int): Product?

    @Query("SELECT * FROM 'ORDER' WHERE CUSTOMER_ID = :customerId AND PICKED_UP = FALSE")
    suspend fun getUnpickedUpOrdersForClient(customerId: String): List<Order>

    @Transaction
    @Query("SELECT * FROM 'ORDER' WHERE CUSTOMER_ID = :customerId AND ORDER_ID = :orderId")
    suspend fun getOrderDetails(
        customerId: String,
        orderId: Int
    ): OrderWithProductsAndQuantityAndVouchers

    // Get customer details
    @Query("SELECT * FROM CUSTOMER WHERE CUSTOMER_ID = :customerId")
    suspend fun getCustomer(customerId: String): Customer

    // Set ticket as used
    @Query("UPDATE TICKET SET USED = 1 WHERE TICKET_ID = :ticketId")
    suspend fun setTicketAsUsed(ticketId: String)

    // Get max order id
    @Query("SELECT MAX(ORDER_ID) FROM `ORDER`")
    suspend fun getMaxOrderId(): Int?

    // Delete customer vouchers
    @Query("DELETE FROM VOUCHER WHERE CUSTOMER_ID = :customerId")
    suspend fun deleteCustomerVouchers(customerId: String)

    // Set order as picked up
    @Query("UPDATE `ORDER` SET PICKED_UP = 1 WHERE ORDER_ID = :orderId")
    suspend fun setOrderAsPickedUp(orderId: Int)


}