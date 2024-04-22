package org.feup.ticketo.data.storage

class TicketoStorage(private val ticketoDao: TicketoDao) {

    suspend fun getEventById(eventId: Int): Event? {
        return ticketoDao.getEvent(eventId)
    }

    suspend fun getCustomerTicketsForEvent(customerId: String, eventId: Int): EventTickets? {
        return ticketoDao.getCustomerTicketsForEvent(customerId, eventId)
    }

    suspend fun getUnusedCustomerTicketsForEvent(customerId: String, eventId: Int): List<Ticket>? {
        return ticketoDao.getUnusedCustomerTicketsForEvent(customerId, eventId)
    }

    suspend fun getUsedCustomerTicketsForEvent(customerId: String, eventId: Int): EventTickets? {
        return ticketoDao.getUsedCustomerTicketsForEvent(customerId, eventId)
    }

    suspend fun getEventsWithUnusedTicketCount(customerId: String): List<EventWithTicketsCount>? {
        return ticketoDao.getEventsWithUnusedTicketCount(customerId)
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

    suspend fun deletePurchasesForClient(customerId: String) {
        ticketoDao.deletePurchasesForClient(customerId)
    }

    suspend fun deleteTicketsForClient(customerId: String) {
        ticketoDao.deleteTicketsForClient(customerId)
    }

    suspend fun deleteEventsForClient(customerId: String) {
        ticketoDao.deleteEventsForClient(customerId)
    }

    suspend fun getPurchasesWithTicketsAndEventsAndVouchersForClient(customerId: String): List<PurchaseWithTicketsAndEventsAndVouchers> {
        return ticketoDao.getPurchasesWithTicketsAndEventsAndVouchersForClient(customerId)
    }

    suspend fun getUnusedVouchersForCustomer(customerId: String): List<Voucher> {
        return ticketoDao.getUnusedVouchersForCustomer(customerId)
    }

    suspend fun getAllProducts(): List<Product> {
        return ticketoDao.getAllProducts()
    }

    suspend fun getUnpickedUpOrdersForClient(customerId: String): List<Order> {
        return ticketoDao.getUnpickedUpOrdersForClient(customerId)
    }

    suspend fun getOrderDetails(
        customerId: String,
        orderId: Int
    ): OrderWithProductsAndQuantityAndVouchers {
        return ticketoDao.getOrderDetails(customerId, orderId)
    }

    suspend fun getCustomer(customerId: String): Customer {
        return ticketoDao.getCustomer(customerId)
    }

    suspend fun setTicketAsUsed(ticketId: String) {
        ticketoDao.setTicketAsUsed(ticketId)
    }

    suspend fun getMaxOrderId(): Int? {
        return ticketoDao.getMaxOrderId()
    }

    suspend fun deleteCustomerVouchers(customerId: String) {
        ticketoDao.deleteCustomerVouchers(customerId)
    }

    suspend fun setOrderAsPickedUp(orderId: Int) {
        ticketoDao.setOrderAsPickedUp(orderId)
    }

    suspend fun deleteUsedTicketsForCustomer(customerId: String){
        ticketoDao.deleteUsedTicketsForCustomer(customerId)
    }

    suspend fun getVoucherById(voucherId : String): Voucher? {
        return ticketoDao.getVoucherById(voucherId)
    }

    suspend fun deleteVoucherById(voucherId: String) {
        ticketoDao.deleteVoucherById(voucherId)
    }
}
