package org.feup.ticketo.data.storage

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

@Database(
    entities = [Customer::class, CreditCard::class, Event::class, Order::class, Product::class, OrderProduct::class, Purchase::class, Ticket::class, Voucher::class],
    views = [OrderProductWithProduct::class],
    version = 5,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3, RenameColumnDateInTicket::class),
        AutoMigration(from = 3, to = 4, RenameColumnDateInTicket2::class),
        AutoMigration(from = 4, to = 5),
    ],
    exportSchema = true
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
                    "ticketo-database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}

@RenameColumn.Entries(
    RenameColumn(
        tableName = "TICKET",
        fromColumnName = "PURCHASE_DATE",
        toColumnName = "DATE"
    )
)
class RenameColumnDateInTicket : AutoMigrationSpec

@RenameColumn.Entries(
    RenameColumn(
        tableName = "TICKET",
        fromColumnName = "DATE",
        toColumnName = "PURCHASE_DATE"
    )
)
class RenameColumnDateInTicket2 : AutoMigrationSpec