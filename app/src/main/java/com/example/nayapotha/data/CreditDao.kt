package com.example.nayapotha.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CreditDao {

    @Insert
    suspend fun insertCredit(credit: Credit)

    @Delete
    suspend fun deleteCredit(credit: Credit)

    @Query("""
        SELECT * FROM credits
        WHERE customerId = :customerId
        ORDER BY creditId DESC
    """)
    suspend fun getCreditsForCustomer(customerId: Int): List<Credit>

    @Query("""
        SELECT COALESCE(SUM(amount), 0)
        FROM credits
        WHERE customerId = :customerId
    """)
    suspend fun getTotalCreditForCustomer(customerId: Int): Double

    @Query("""
        SELECT COALESCE(SUM(amount), 0)
        FROM credits
    """)
    suspend fun getTotalCreditAmount(): Double
}