package com.example.nayapotha

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class CreditConfirmationActivity : AppCompatActivity() {

    private var customerId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_credit_confirmation)

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

        customerId =
            intent.getIntExtra(
                "CUSTOMER_ID",
                -1
            )

        val customerName =
            intent.getStringExtra(
                "CUSTOMER_NAME"
            ) ?: "Customer"

        val amountAdded =
            intent.getDoubleExtra(
                "AMOUNT_ADDED",
                0.0
            )

        val previousBalance =
            intent.getDoubleExtra(
                "PREVIOUS_BALANCE",
                0.0
            )

        val newBalance =
            intent.getDoubleExtra(
                "NEW_BALANCE",
                0.0
            )

        val tvCustomerName =
            findViewById<TextView>(R.id.tvCustomerName)

        val tvAmountAdded =
            findViewById<TextView>(R.id.tvAmountAdded)

        val tvPreviousBalance =
            findViewById<TextView>(R.id.tvPreviousBalance)

        val tvNewBalance =
            findViewById<TextView>(R.id.tvNewBalance)

        val btnDone =
            findViewById<MaterialButton>(R.id.btnDone)

        val btnViewCustomer =
            findViewById<MaterialButton>(R.id.btnViewCustomer)

        val navHome =
            findViewById<LinearLayout>(R.id.navHome)

        val navCustomers =
            findViewById<LinearLayout>(R.id.navCustomers)

        val navSettings =
            findViewById<LinearLayout>(R.id.navSettings)

        tvCustomerName.text =
            customerName

        tvAmountAdded.text =
            "+ ${formatAmount(amountAdded)}"

        tvPreviousBalance.text =
            formatAmount(previousBalance)

        tvNewBalance.text =
            formatAmount(newBalance)

        btnDone.setOnClickListener {
            openDashboard()
        }

        btnViewCustomer.setOnClickListener {

            if (customerId == -1) {
                openCustomerList()
                return@setOnClickListener
            }

            val intent =
                Intent(
                    this,
                    CustomerDetailsActivity::class.java
                )

            intent.putExtra(
                "CUSTOMER_ID",
                customerId
            )

            startActivity(intent)
            finish()
        }

        navHome.setOnClickListener {
            openDashboard()
        }

        navCustomers.setOnClickListener {
            openCustomerList()
        }

        navSettings.setOnClickListener {

            val intent =
                Intent(
                    this,
                    SettingsActivity::class.java
                )

            startActivity(intent)
            finish()
        }
    }

    private fun openDashboard() {

        val intent =
            Intent(
                this,
                DashboardActivity::class.java
            ).apply {

                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

        startActivity(intent)
        finish()
    }

    private fun openCustomerList() {

        val intent =
            Intent(
                this,
                CustomerListActivity::class.java
            )

        startActivity(intent)
        finish()
    }

    private fun formatAmount(
        amount: Double
    ): String {

        return if (amount % 1.0 == 0.0) {
            "Rs. ${amount.toLong()}"
        } else {
            "Rs. %.2f".format(amount)
        }
    }
}