package com.example.nayapotha.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "credits",
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
data class Credit(
    @PrimaryKey(autoGenerate = true)
    val creditId: Int = 0,

    val customerId: Int,
    val description: String,
    val amount: Double,
    val date: String,
    val note: String = ""
)