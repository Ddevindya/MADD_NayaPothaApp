package com.example.nayapotha

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.nayapotha.data.AppDatabase
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class CustomerDetailsActivity : AppCompatActivity() {

    private var customerId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer_details)

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

        customerId = intent.getIntExtra("CUSTOMER_ID", -1)

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

        val btnEditCustomer =
            findViewById<TextView>(R.id.btnEditCustomer)

        val btnAddCredit =
            findViewById<MaterialButton>(R.id.btnAddCredit)

        val btnRecordPayment =
            findViewById<MaterialButton>(R.id.btnRecordPayment)

        val btnViewAllHistory =
            findViewById<TextView>(R.id.btnViewAllHistory)

        btnBack.setOnClickListener {
            finish()
        }

        btnEditCustomer.setOnClickListener {
            val intent = Intent(
                this,
                EditCustomerActivity::class.java
            )

            intent.putExtra(
                "CUSTOMER_ID",
                customerId
            )

            startActivity(intent)
        }

        btnAddCredit.setOnClickListener {
            val intent = Intent(
                this,
                AddCreditActivity::class.java
            )

            intent.putExtra(
                "CUSTOMER_ID",
                customerId
            )

            startActivity(intent)
        }

        btnRecordPayment.setOnClickListener {
            val intent = Intent(
                this,
                RecordPaymentActivity::class.java
            )

            intent.putExtra(
                "CUSTOMER_ID",
                customerId
            )

            startActivity(intent)
        }

        btnViewAllHistory.setOnClickListener {
            val intent = Intent(
                this,
                TransactionHistoryActivity::class.java
            )

            intent.putExtra(
                "CUSTOMER_ID",
                customerId
            )

            startActivity(intent)
        }

        loadCustomerDetails()
    }

    override fun onResume() {
        super.onResume()

        if (customerId != -1) {
            loadCustomerDetails()
        }
    }

    private fun loadCustomerDetails() {

        lifecycleScope.launch {

            val database =
                AppDatabase.getDatabase(applicationContext)

            val customer =
                database.customerDao()
                    .getCustomerById(customerId)

            if (customer == null) {
                Toast.makeText(
                    this@CustomerDetailsActivity,
                    "Customer not found",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
                return@launch
            }

            val credits =
                database.creditDao()
                    .getCreditsForCustomer(customerId)

            val payments =
                database.paymentDao()
                    .getPaymentsForCustomer(customerId)

            val totalCredit =
                database.creditDao()
                    .getTotalCreditForCustomer(customerId)

            val totalPaid =
                database.paymentDao()
                    .getTotalPaidForCustomer(customerId)

            val remaining =
                (totalCredit - totalPaid)
                    .coerceAtLeast(0.0)

            val tvCustomerName =
                findViewById<TextView>(R.id.tvCustomerName)

            val tvCustomerPhone =
                findViewById<TextView>(R.id.tvCustomerPhone)

            val tvTotalCredit =
                findViewById<TextView>(R.id.tvTotalCredit)

            val tvTotalPaid =
                findViewById<TextView>(R.id.tvTotalPaid)

            val tvRemaining =
                findViewById<TextView>(R.id.tvRemaining)

            val tvActivityBalance =
                findViewById<TextView>(R.id.tvActivityBalance)

            val tvRecentCreditDescription =
                findViewById<TextView>(R.id.tvRecentCreditDescription)

            val tvRecentCreditDate =
                findViewById<TextView>(R.id.tvRecentCreditDate)

            val tvRecentCreditAmount =
                findViewById<TextView>(R.id.tvRecentCreditAmount)

            val tvRecentPaymentTitle =
                findViewById<TextView>(R.id.tvRecentPaymentTitle)

            val tvRecentPaymentDate =
                findViewById<TextView>(R.id.tvRecentPaymentDate)

            val tvRecentPaymentAmount =
                findViewById<TextView>(R.id.tvRecentPaymentAmount)

            tvCustomerName.text =
                customer.name

            tvCustomerPhone.text =
                customer.phoneNumber

            tvTotalCredit.text =
                formatAmount(totalCredit)

            tvTotalPaid.text =
                formatAmount(totalPaid)

            tvRemaining.text =
                formatAmount(remaining)

            tvActivityBalance.text =
                formatAmount(remaining)

            val latestCredit =
                credits.firstOrNull()

            if (latestCredit != null) {
                tvRecentCreditDescription.text =
                    latestCredit.description

                tvRecentCreditDate.text =
                    latestCredit.date

                tvRecentCreditAmount.text =
                    "+ ${formatAmount(latestCredit.amount)}"
            } else {
                tvRecentCreditDescription.text =
                    "No credit added yet"

                tvRecentCreditDate.text =
                    "-"

                tvRecentCreditAmount.text =
                    "Rs. 0"
            }

            val latestPayment =
                payments.firstOrNull()

            if (latestPayment != null) {

                tvRecentPaymentTitle.text =
                    if (latestPayment.note.isBlank()) {
                        "Payment Received"
                    } else {
                        latestPayment.note
                    }

                tvRecentPaymentDate.text =
                    latestPayment.date

                tvRecentPaymentAmount.text =
                    "- ${formatAmount(latestPayment.amount)}"

            } else {

                tvRecentPaymentTitle.text =
                    "No payment recorded yet"

                tvRecentPaymentDate.text =
                    "-"

                tvRecentPaymentAmount.text =
                    "Rs. 0"
            }
        }
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