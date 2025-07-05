package com.meshkipli.smarttravel.data.repository // Or your preferred package

import com.meshkipli.smarttravel.data.local.db.dao.ExpenseDao
import com.meshkipli.smarttravel.data.local.db.entities.Expense
import kotlinx.coroutines.flow.Flow
import java.util.Date

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
    val totalExpenses: Flow<Double?> = expenseDao.getTotalExpenses()
    val firstExpenseDate: Flow<Date?> = expenseDao.getFirstExpenseDate()

    suspend fun insert(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    suspend fun update(expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    suspend fun delete(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }

    fun getExpenseById(id: Int): Flow<Expense?> {
        return expenseDao.getExpenseById(id)
    }

    fun getExpensesBetweenDates(startDate: Date, endDate: Date): Flow<List<Expense>> {
        return expenseDao.getExpensesBetweenDates(startDate, endDate)
    }
}
