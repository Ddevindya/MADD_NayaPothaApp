package com.example.nayapotha.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["customerId"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["customerId"])]
)
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val paymentId: Int = 0,

    val customerId: Int,
    val amount: Double,
    val date: String,
    val note: String = ""
)