package org.feup.ticketo.data.storage

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "CUSTOMER")
data class Customer(
    @PrimaryKey
    @ColumnInfo(name = "CUSTOMER_ID")
    val customer_id: String? = null,
    @ColumnInfo(name = "USERNAME")
    val username: String? = null,
    @ColumnInfo(name = "PASSWORD")
    val password: String? = null,
    @ColumnInfo(name = "TAX_NUMBER")
    val tax_number: Long? = null,
    @ColumnInfo(name = "PUBLIC_KEY")
    val public_key: String? = null,
    @ColumnInfo(name = "NAME")
    val name: String? = null
)

@Entity(
    tableName = "CREDIT_CARD",
    foreignKeys = [ForeignKey(
        entity = Customer::class,
        parentColumns = ["CUSTOMER_ID"],
        childColumns = ["CUSTOMER_ID"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class CreditCard(
    @PrimaryKey
    @ColumnInfo(name = "CREDIT_CARD_ID")
    val credit_card_id: Int? = null,
    @ColumnInfo(name = "CUSTOMER_ID")
    val customer_id: String? = null,
    @ColumnInfo(name = "TYPE")
    val type: String? = null,
    @ColumnInfo(name = "NUMBER")
    val number: String? = null,
    @ColumnInfo(name = "VALIDITY")
    val validity: String? = null
)

@Entity(tableName = "EVENT")
data class Event(
    @PrimaryKey
    @ColumnInfo(name = "EVENT_ID")
    val event_id: Int? = null,
    @ColumnInfo(name = "NAME")
    val name: String? = null,
    @ColumnInfo(name = "DATE")
    val date: String? = null,
    @ColumnInfo(name = "PICTURE")
    val picture: ByteArray? = null,
    @ColumnInfo(name = "PRICE")
    val price: Float? = null
)

@Entity(
    tableName = "ORDER",
    foreignKeys = [ForeignKey(
        entity = Customer::class,
        parentColumns = ["CUSTOMER_ID"],
        childColumns = ["CUSTOMER_ID"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Order(
    @PrimaryKey
    @ColumnInfo(name = "ORDER_ID")
    val order_id: Int? = null,
    @ColumnInfo(name = "CUSTOMER_ID")
    val customer_id: String? = null,
    @ColumnInfo(name = "DATE")
    val date: String? = null,
    @ColumnInfo(name = "PAID")
    val paid: Boolean? = null,
    @ColumnInfo(name = "PICKED_UP")
    val picked_up: Boolean? = null,
    @ColumnInfo(name = "TOTAL_PRICE")
    val total_price: Float? = null
)

@Entity(tableName = "PRODUCT")
data class Product(
    @PrimaryKey
    @ColumnInfo(name = "PRODUCT_ID")
    val product_id: Int? = null,
    @ColumnInfo(name = "AVAILABLE")
    val available: Boolean? = null,
    @ColumnInfo(name = "NAME")
    val name: String? = null,
    @ColumnInfo(name = "DESCRIPTION")
    val description: String? = null,
    @ColumnInfo(name = "PRICE")
    val price: Float? = null
)

@Entity(
    tableName = "ORDER_PRODUCT",
    primaryKeys = ["PRODUCT_ID", "ORDER_ID"],
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["PRODUCT_ID"],
            childColumns = ["PRODUCT_ID"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Order::class,
            parentColumns = ["ORDER_ID"],
            childColumns = ["ORDER_ID"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class OrderProduct(
    @ColumnInfo(name = "PRODUCT_ID")
    val product_id: Int? = null,
    @ColumnInfo(name = "ORDER_ID")
    val order_id: Int? = null,
    @ColumnInfo(name = "QUANTITY")
    val quantity: Int? = null
)

@Entity(
    tableName = "PURCHASE",
    foreignKeys = [ForeignKey(
        entity = Customer::class,
        parentColumns = ["CUSTOMER_ID"],
        childColumns = ["CUSTOMER_ID"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)

data class Purchase(
    @PrimaryKey
    @ColumnInfo(name = "PURCHASE_ID")
    val purchase_id: Int? = null,
    @ColumnInfo(name = "CUSTOMER_ID")
    val customer_id: String? = null,
    @ColumnInfo(name = "DATE")
    val date: String? = null,
    @ColumnInfo(name = "TOTAL_PRICE")
    val total_price: Float? = null
)

@Entity(
    tableName = "TICKET",
    foreignKeys = [
        ForeignKey(
            entity = Purchase::class,
            parentColumns = ["PURCHASE_ID"],
            childColumns = ["PURCHASE_ID"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Event::class,
            parentColumns = ["EVENT_ID"],
            childColumns = ["EVENT_ID"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Ticket(
    @PrimaryKey
    @ColumnInfo(name = "TICKET_ID")
    val ticket_id: String? = null,
    @ColumnInfo(name = "PURCHASE_ID")
    val purchase_id: Int? = null,
    @ColumnInfo(name = "EVENT_ID")
    val event_id: Int? = null,
    @ColumnInfo(name = "PURCHASE_DATE")
    val purchase_date: String? = null,
    @ColumnInfo(name = "USED")
    val used: Boolean? = null,
    @ColumnInfo(name = "QRCODE")
    val qrcode: String? = null,
    @ColumnInfo(name = "PLACE")
    val place: String? = null
)

@Entity(
    tableName = "VOUCHER",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["CUSTOMER_ID"],
            childColumns = ["CUSTOMER_ID"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Order::class,
            parentColumns = ["ORDER_ID"],
            childColumns = ["ORDER_ID"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["PRODUCT_ID"],
            childColumns = ["PRODUCT_ID"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Voucher(
    @PrimaryKey
    @ColumnInfo(name = "VOUCHER_ID")
    val voucher_id: String? = null,
    @ColumnInfo(name = "CUSTOMER_ID")
    val customer_id: String? = null,
    @ColumnInfo(name = "PRODUCT_ID")
    val product_id: Int? = null,
    @ColumnInfo(name = "ORDER_ID")
    val order_id: Int? = null,
    @ColumnInfo(name = "TYPE")
    val type: String? = null,
    @ColumnInfo(name = "DESCRIPTION")
    val description: String? = null,
    @ColumnInfo(name = "REDEEMED")
    val redeemed: Boolean? = null
)

data class EventWithTicketCount(
    val event: Event,
    val ticketsBought: Int
)

data class PurchaseWithTicketsAndEvents(
    @Embedded val purchase: Purchase,
    @Relation(
        parentColumn = "PURCHASE_ID",
        entityColumn = "PURCHASE_ID"
    )
    val tickets: List<Ticket>,
    @Relation(
        parentColumn = "EVENT_ID",
        entityColumn = "EVENT_ID"
    )
    val events: List<Event>
)

data class ProductWithQuantity(
    @Embedded val product: Product,
    @ColumnInfo(name = "QUANTITY") val quantity: Int
)

data class OrderWithProductsAndQuantityAndVouchers(
    @Embedded val order: Order,
    @Relation(
        parentColumn = "ORDER_ID",
        entityColumn = "PRODUCT_ID",
        associateBy = Junction(OrderProduct::class)
    )
    val productsWithQuantity: List<ProductWithQuantity>,
    @Relation(
        parentColumn = "ORDER_ID",
        entityColumn = "ORDER_ID"
    )
    val vouchers: List<Voucher>
)