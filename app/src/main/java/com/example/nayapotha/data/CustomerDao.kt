package com.example.nayapotha.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CustomerDao {

    @Insert
    suspend fun insertCustomer(customer: Customer)

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("SELECT * FROM customers ORDER BY name ASC")
    suspend fun getAllCustomers(): List<Customer>

    @Query("""
        SELECT * FROM customers
        WHERE name LIKE '%' || :searchText || '%'
        OR phoneNumber LIKE '%' || :searchText || '%'
        ORDER BY name ASC
    """)
    suspend fun searchCustomers(searchText: String): List<Customer>

    @Query("SELECT * FROM customers WHERE customerId = :customerId LIMIT 1")
    suspend fun getCustomerById(customerId: Int): Customer?

    @Query("SELECT COUNT(*) FROM customers")
    suspend fun getCustomerCount(): Int
}