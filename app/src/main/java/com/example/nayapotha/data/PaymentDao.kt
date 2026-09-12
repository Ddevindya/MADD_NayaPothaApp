package com.example.nayapotha.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PaymentDao {

    @Insert
    suspend fun insertPayment(payment: Payment)

    @Delete
    suspend fun deletePayment(payment: Payment)

    @Query("""
        SELECT * FROM payments
        WHERE customerId = :customerId
        ORDER BY paymentId DESC
    """)
    suspend fun getPaymentsForCustomer(customerId: Int): List<Payment>

    @Query("""
        SELECT COALESCE(SUM(amount), 0)
        FROM payments
        WHERE customerId = :customerId
    """)
    suspend fun getTotalPaidForCustomer(customerId: Int): Double

    @Query("""
        SELECT COALESCE(SUM(amount), 0)
        FROM payments
    """)
    suspend fun getTotalPaymentAmount(): Double
}