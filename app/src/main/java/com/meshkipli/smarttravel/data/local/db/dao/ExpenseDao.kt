package com.meshkipli.smarttravel.data.local.db.dao // Or your preferred package

import androidx.room.*
import com.meshkipli.smarttravel.data.local.db.entities.Expense
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>> // Observe changes with Flow

    @Query("SELECT * FROM expenses WHERE id = :id")
    fun getExpenseById(id: Int): Flow<Expense?>

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpenses(): Flow<Double?> // Can be null if no expenses

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    fun getExpensesBetweenDates(startDate: Date, endDate: Date): Flow<List<Expense>>

    // For daily average, we usually need the first expense date and total
    @Query("SELECT MIN(date) FROM expenses")
    fun getFirstExpenseDate(): Flow<Date?>
}
