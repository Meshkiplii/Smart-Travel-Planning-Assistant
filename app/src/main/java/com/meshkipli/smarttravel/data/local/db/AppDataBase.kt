package com.meshkipli.smarttravel.data.local.db // Or your preferred package

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.meshkipli.smarttravel.data.local.db.dao.ExpenseDao
import com.meshkipli.smarttravel.data.local.db.entities.Expense
import com.meshkipli.smarttravel.data.local.db.utils.DateConverter // We'll create this next

@Database(entities = [Expense::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_travel_database"
                )
                    // Add migrations here if you change the schema in the future
                    .fallbackToDestructiveMigration() // Not recommended for production, use proper migrations
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
