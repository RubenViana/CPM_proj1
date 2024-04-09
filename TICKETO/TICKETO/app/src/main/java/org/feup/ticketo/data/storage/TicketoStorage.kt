package org.feup.ticketo.data.storage

class TicketoStorage(private val ticketoDao: TicketoDao) {

    suspend fun getCustomerTicketsForEvent(customerId: String, eventId: Int): List<Ticket>? {
        return ticketoDao.getCustomerTicketsForEvent(customerId, eventId)
    }

    suspend fun getEvent(eventId: Int): Event? {
        return ticketoDao.getEvent(eventId)
    }

    suspend fun getAllFutureEvents(date: String): List<Event>? {
        return ticketoDao.getAllFutureEvents(date)
    }

    suspend fun getEventsWithTicketCount(customerId: String): List<EventWithTicketsCount>? {
        return ticketoDao.getEventsWithTicketCount(customerId)
    }

    suspend fun insertCustomer(customer: Customer) {
        ticketoDao.insertCustomer(customer)
    }

    suspend fun insertCreditCard(creditCard: CreditCard) {
        ticketoDao.insertCreditCard(creditCard)
    }

    suspend fun insertEvent(event: Event) {
        ticketoDao.insertEvent(event)
    }

    suspend fun insertOrder(order: Order) {
        ticketoDao.insertOrder(order)
    }

    suspend fun insertProduct(product: Product) {
        ticketoDao.insertProduct(product)
    }

    suspend fun insertOrderProduct(orderProduct: OrderProduct) {
        ticketoDao.insertOrderProduct(orderProduct)
    }

    suspend fun insertPurchase(purchase: Purchase) {
        ticketoDao.insertPurchase(purchase)
    }

    suspend fun insertTicket(ticket: Ticket) {
        ticketoDao.insertTicket(ticket)
    }

    suspend fun insertVoucher(voucher: Voucher) {
        ticketoDao.insertVoucher(voucher)
    }

    suspend fun getVouchersForClient(customerId: String): List<Voucher> {
        return ticketoDao.getVouchersForClient(customerId)
    }

    suspend fun deletePurchasesForClient(customerId: String) {
        ticketoDao.deletePurchasesForClient(customerId)
    }

    suspend fun deleteTicketsForClient(customerId: String) {
        ticketoDao.deleteTicketsForClient(customerId)
    }

    suspend fun deleteEventsForClient(customerId: String) {
        ticketoDao.deleteEventsForClient(customerId)
    }

    suspend fun getPurchasesWithTicketsAndEventsForClient(customerId: String): List<PurchaseWithTicketsAndEvents> {
        return ticketoDao.getPurchasesWithTicketsAndEventsForClient(customerId)
    }

    suspend fun getVouchersForCustomer(customerId: String): List<Voucher> {
        return ticketoDao.getVouchersForCustomer(customerId)
    }

    suspend fun getAllProducts(): List<Product> {
        return ticketoDao.getAllProducts()
    }

    suspend fun getOrdersWithProductsAndVouchersForClient(customerId: String): List<OrderWithProductsAndQuantityAndVouchers> {
        return ticketoDao.getOrdersWithProductsAndVouchersForClient(customerId)
    }
}