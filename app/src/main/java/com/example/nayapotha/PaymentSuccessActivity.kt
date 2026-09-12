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

class PaymentSuccessActivity : AppCompatActivity() {

    private var customerId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment_success)

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

        val paymentAmount =
            intent.getDoubleExtra(
                "PAYMENT_AMOUNT",
                0.0
            )

        val previousBalance =
            intent.getDoubleExtra(
                "PREVIOUS_BALANCE",
                0.0
            )

        val remainingBalance =
            intent.getDoubleExtra(
                "REMAINING_BALANCE",
                0.0
            )

        val tvCustomerName =
            findViewById<TextView>(R.id.tvCustomerName)

        val tvPaymentReceived =
            findViewById<TextView>(R.id.tvPaymentReceived)

        val tvPreviousBalance =
            findViewById<TextView>(R.id.tvPreviousBalance)

        val tvRemainingBalance =
            findViewById<TextView>(R.id.tvRemainingBalance)

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

        tvPaymentReceived.text =
            "- ${formatAmount(paymentAmount)}"

        tvPreviousBalance.text =
            formatAmount(previousBalance)

        tvRemainingBalance.text =
            formatAmount(remainingBalance)

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