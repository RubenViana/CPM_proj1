package org.feup.ticketo.data.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Customer::class, CreditCard::class, Event::class, Order::class, Product::class, OrderProduct::class, Purchase::class, Ticket::class, Voucher::class],
    version = 1
)
abstract class TicketoDatabase : RoomDatabase() {
    abstract fun ticketDao(): TicketoDao

    companion object {
        @Volatile
        private var INSTANCE: TicketoDatabase? = null

        fun getInstance(context: Context): TicketoDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TicketoDatabase::class.java, "ticketo_database.db"
            ).build()
    }

}