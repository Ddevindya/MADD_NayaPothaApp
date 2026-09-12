package com.example.nayapotha

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.nayapotha.data.AppDatabase
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

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

        val navCustomers =
            findViewById<LinearLayout>(R.id.navCustomers)

        val navSettings =
            findViewById<LinearLayout>(R.id.navSettings)

        val btnAddCustomer =
            findViewById<LinearLayout>(R.id.btnAddCustomer)

        val btnAddCredit =
            findViewById<LinearLayout>(R.id.btnAddCredit)

        val btnRecordPayment =
            findViewById<LinearLayout>(R.id.btnRecordPayment)

        navCustomers.setOnClickListener {
            openCustomerList()
        }

        navSettings.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }

        btnAddCustomer.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddCustomerActivity::class.java
                )
            )
        }

        btnAddCredit.setOnClickListener {
            openCustomerList(
                "ADD_CREDIT"
            )
        }

        btnRecordPayment.setOnClickListener {
            openCustomerList(
                "RECORD_PAYMENT"
            )
        }
    }

    override fun onResume() {
        super.onResume()

        loadDashboardData()
    }

    private fun openCustomerList(
        action: String? = null
    ) {

        val intent =
            Intent(
                this,
                CustomerListActivity::class.java
            )

        if (action != null) {
            intent.putExtra(
                "CUSTOMER_ACTION",
                action
            )
        }

        startActivity(intent)
    }

    private fun loadDashboardData() {

        lifecycleScope.launch {

            val database =
                AppDatabase.getDatabase(
                    applicationContext
                )

            val customerCount =
                database.customerDao()
                    .getCustomerCount()

            val totalCredit =
                database.creditDao()
                    .getTotalCreditAmount()

            val totalPayments =
                database.paymentDao()
                    .getTotalPaymentAmount()

            val totalOutstanding =
                (totalCredit - totalPayments)
                    .coerceAtLeast(0.0)

            val tvTotalOutstanding =
                findViewById<TextView>(
                    R.id.tvTotalOutstanding
                )

            val tvOutstandingCustomerCount =
                findViewById<TextView>(
                    R.id.tvOutstandingCustomerCount
                )

            val tvTotalCustomers =
                findViewById<TextView>(
                    R.id.tvTotalCustomers
                )

            val tvPaymentsReceived =
                findViewById<TextView>(
                    R.id.tvPaymentsReceived
                )

            tvTotalOutstanding.text =
                formatAmount(
                    totalOutstanding
                )

            tvOutstandingCustomerCount.text =
                if (customerCount == 1) {
                    "across 1 customer"
                } else {
                    "across $customerCount customers"
                }

            tvTotalCustomers.text =
                if (customerCount == 1) {
                    "1 Customer"
                } else {
                    "$customerCount Customers"
                }

            tvPaymentsReceived.text =
                formatAmount(
                    totalPayments
                )
        }
    }

    private fun formatAmount(
        amount: Double
    ): String {

        return if (
            amount % 1.0 == 0.0
        ) {
            "Rs. ${amount.toLong()}"
        } else {
            "Rs. %.2f".format(
                amount
            )
        }
    }
}