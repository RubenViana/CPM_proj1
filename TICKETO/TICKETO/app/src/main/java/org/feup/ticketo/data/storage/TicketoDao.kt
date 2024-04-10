package org.feup.ticketo.data.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface TicketoDao {

    // Get customer tickets for an event
    @Query("SELECT * FROM TICKET WHERE EVENT_ID = :eventId AND PURCHASE_ID IN (SELECT PURCHASE_ID FROM PURCHASE WHERE CUSTOMER_ID = :customerId)")
    suspend fun getCustomerTicketsForEvent(customerId: String, eventId: Int): List<Ticket>?

    // Get customer tickets for an event
    @Query("SELECT * FROM EVENT WHERE EVENT_ID = :eventId")
    suspend fun getEvent(eventId: Int): Event?

    // Get all future events
    @Query("SELECT * FROM EVENT WHERE DATE >= :date")
    suspend fun getAllFutureEvents(date: String): List<Event>?
    // val currentDate = LocalDate.now().toString() // Current date in string format

    // Get events for which a customer has purchased tickets along with the count of tickets bought for each event
    @Query("""
        SELECT e.*, COUNT(t.TICKET_ID) AS tickets_count
        FROM EVENT e 
        INNER JOIN TICKET t ON e.EVENT_ID = t.EVENT_ID 
        INNER JOIN PURCHASE p ON t.PURCHASE_ID = p.PURCHASE_ID 
        WHERE p.CUSTOMER_ID = :customerId 
        GROUP BY e.EVENT_ID
    """)
    suspend fun getEventsWithTicketCount(customerId: String): List<EventWithTicketsCount>?

    @Insert
    suspend fun insertCustomer(customer: Customer)

    // Insert a credit card into the database
    @Insert
    suspend fun insertCreditCard(creditCard: CreditCard)

    // Insert an event into the database
    @Insert
    suspend fun insertEvent(event: Event)

    // Insert an order into the database
    @Insert
    suspend fun insertOrder(order: Order)

    // Insert a product into the database
    @Insert
    suspend fun insertProduct(product: Product)

    // Insert an order product into the database
    @Insert
    suspend fun insertOrderProduct(orderProduct: OrderProduct)

    // Insert a purchase into the database
    @Insert
    suspend fun insertPurchase(purchase: Purchase)

    // Insert a ticket into the database
    @Insert
    suspend fun insertTicket(ticket: Ticket)

    // Insert a voucher into the database
    @Insert
    suspend fun insertVoucher(voucher: Voucher)

    // Get all vouchers for a client
    @Query("SELECT * FROM VOUCHER WHERE CUSTOMER_ID = :customerId")
    suspend fun getVouchersForClient(customerId: String): List<Voucher>

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
    @Query("SELECT * FROM VOUCHER WHERE CUSTOMER_ID = :customerId")
    suspend fun getVouchersForCustomer(customerId: String): List<Voucher>

    // Get all products
    @Query("SELECT * FROM PRODUCT")
    suspend fun getAllProducts(): List<Product>

    @Transaction
    @Query("SELECT * FROM 'ORDER' WHERE CUSTOMER_ID = :customerId")
    suspend fun getOrdersWithProductsAndVouchersForClient(customerId: String): List<OrderWithProductsAndQuantityAndVouchers>

}