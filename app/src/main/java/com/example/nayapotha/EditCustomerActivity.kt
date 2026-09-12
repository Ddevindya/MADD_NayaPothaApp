package com.example.nayapotha

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.nayapotha.data.AppDatabase
import com.example.nayapotha.data.Customer
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class EditCustomerActivity : AppCompatActivity() {

    private var customerId: Int = -1

    private lateinit var etFullName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var etNotes: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_customer)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        customerId = intent.getIntExtra(
            "CUSTOMER_ID",
            -1
        )

        if (customerId == -1) {
            Toast.makeText(
                this,
                "Customer information not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        val btnBack =
            findViewById<TextView>(R.id.btnBack)

        val btnCancel =
            findViewById<TextView>(R.id.btnCancel)

        val btnUpdateCustomer =
            findViewById<MaterialButton>(R.id.btnUpdateCustomer)

        val btnDeleteCustomer =
            findViewById<MaterialButton>(R.id.btnDeleteCustomer)

        etFullName =
            findViewById(R.id.etFullName)

        etPhone =
            findViewById(R.id.etPhone)

        etAddress =
            findViewById(R.id.etAddress)

        etNotes =
            findViewById(R.id.etNotes)

        btnBack.setOnClickListener {
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnUpdateCustomer.setOnClickListener {
            updateCustomer()
        }

        btnDeleteCustomer.setOnClickListener {
            showDeleteConfirmation()
        }

        loadCustomer()
    }

    private fun loadCustomer() {

        lifecycleScope.launch {

            val database =
                AppDatabase.getDatabase(
                    applicationContext
                )

            val customer =
                database.customerDao()
                    .getCustomerById(
                        customerId
                    )

            if (customer == null) {
                Toast.makeText(
                    this@EditCustomerActivity,
                    "Customer not found",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
                return@launch
            }

            etFullName.setText(
                customer.name
            )

            etPhone.setText(
                customer.phoneNumber
            )

            etAddress.setText(
                customer.address
            )

            etNotes.setText(
                customer.notes
            )
        }
    }

    private fun updateCustomer() {

        val name =
            etFullName.text
                .toString()
                .trim()

        val phone =
            etPhone.text
                .toString()
                .trim()

        val address =
            etAddress.text
                .toString()
                .trim()

        val notes =
            etNotes.text
                .toString()
                .trim()

        if (name.isEmpty()) {
            etFullName.error =
                "Please enter customer name"

            etFullName.requestFocus()
            return
        }

        if (phone.isEmpty()) {
            etPhone.error =
                "Please enter phone number"

            etPhone.requestFocus()
            return
        }

        if (!phone.all { it.isDigit() }) {
            etPhone.error =
                "Phone number must contain digits only"

            etPhone.requestFocus()
            return
        }

        if (phone.length != 10) {
            etPhone.error =
                "Phone number must contain exactly 10 digits"

            etPhone.requestFocus()
            return
        }

        val updatedCustomer =
            Customer(
                customerId = customerId,
                name = name,
                phoneNumber = phone,
                address = address,
                notes = notes
            )

        lifecycleScope.launch {

            val database =
                AppDatabase.getDatabase(
                    applicationContext
                )

            database.customerDao()
                .updateCustomer(
                    updatedCustomer
                )

            Toast.makeText(
                this@EditCustomerActivity,
                "Customer updated successfully",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }
    }

    private fun showDeleteConfirmation() {

        AlertDialog.Builder(this)
            .setTitle(
                "Delete Customer"
            )
            .setMessage(
                "Are you sure you want to delete this customer? All related credit and payment records will also be deleted."
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                deleteCustomer()
            }
            .show()
    }

    private fun deleteCustomer() {

        lifecycleScope.launch {

            val database =
                AppDatabase.getDatabase(
                    applicationContext
                )

            val customer =
                database.customerDao()
                    .getCustomerById(
                        customerId
                    )

            if (customer == null) {
                Toast.makeText(
                    this@EditCustomerActivity,
                    "Customer not found",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
                return@launch
            }

            database.customerDao()
                .deleteCustomer(
                    customer
                )

            Toast.makeText(
                this@EditCustomerActivity,
                "Customer deleted successfully",
                Toast.LENGTH_SHORT
            ).show()

            val intent =
                Intent(
                    this@EditCustomerActivity,
                    CustomerListActivity::class.java
                ).apply {

                    flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                }

            startActivity(intent)
            finish()
        }
    }
}