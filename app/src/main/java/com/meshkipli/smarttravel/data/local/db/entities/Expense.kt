package com.meshkipli.smarttravel.data.local.db.entities // Or your preferred package

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val category: String, // You might want to make this an enum or relate it to an ExpenseCategoryEntity
    val date: Date, // Stores the exact time of the expense
    val description: String? = null // Optional description
)