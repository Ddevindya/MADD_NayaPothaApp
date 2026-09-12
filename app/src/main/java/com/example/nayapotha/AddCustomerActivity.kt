package com.example.nayapotha

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

class AddCustomerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_customer)

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

        val btnBack =
            findViewById<TextView>(R.id.btnBack)

        val btnCancel =
            findViewById<TextView>(R.id.btnCancel)

        val btnSaveCustomer =
            findViewById<MaterialButton>(R.id.btnSaveCustomer)

        val etFullName =
            findViewById<EditText>(R.id.etFullName)

        val etPhone =
            findViewById<EditText>(R.id.etPhone)

        val etAddress =
            findViewById<EditText>(R.id.etAddress)

        val etNotes =
            findViewById<EditText>(R.id.etNotes)

        btnBack.setOnClickListener {
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnSaveCustomer.setOnClickListener {

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
                return@setOnClickListener
            }

            if (phone.isEmpty()) {
                etPhone.error =
                    "Please enter phone number"

                etPhone.requestFocus()
                return@setOnClickListener
            }

            if (!phone.all { it.isDigit() }) {
                etPhone.error =
                    "Phone number must contain digits only"

                etPhone.requestFocus()
                return@setOnClickListener
            }

            if (phone.length != 10) {
                etPhone.error =
                    "Phone number must contain exactly 10 digits"

                etPhone.requestFocus()
                return@setOnClickListener
            }

            val customer =
                Customer(
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
                    .insertCustomer(customer)

                Toast.makeText(
                    this@AddCustomerActivity,
                    "Customer saved successfully",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        }
    }
}