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
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: TicketoDatabase? = null

        fun getDatabase(context: Context): TicketoDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TicketoDatabase::class.java,
                    "word_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}