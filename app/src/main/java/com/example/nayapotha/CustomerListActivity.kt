package com.example.nayapotha

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.nayapotha.data.AppDatabase
import com.example.nayapotha.data.Customer
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

class CustomerListActivity : AppCompatActivity() {

    private lateinit var customerListContainer: LinearLayout
    private lateinit var etSearchCustomer: EditText

    private var customerAction: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer_list)

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

        customerAction =
            intent.getStringExtra("CUSTOMER_ACTION")

        customerListContainer =
            findViewById(R.id.customerListContainer)

        etSearchCustomer =
            findViewById(R.id.etSearchCustomer)

        val navHome =
            findViewById<LinearLayout>(R.id.navHome)

        val navSettings =
            findViewById<LinearLayout>(R.id.navSettings)

        val fabAddCustomer =
            findViewById<MaterialCardView>(R.id.fabAddCustomer)

        navHome.setOnClickListener {

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

        navSettings.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )

            finish()
        }

        fabAddCustomer.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AddCustomerActivity::class.java
                )
            )
        }

        etSearchCustomer.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    searchCustomers(
                        s?.toString()?.trim() ?: ""
                    )
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()

        refreshCustomerList()
    }

    private fun refreshCustomerList() {

        val searchText =
            if (::etSearchCustomer.isInitialized) {

                etSearchCustomer.text
                    .toString()
                    .trim()

            } else {

                ""
            }

        if (searchText.isEmpty()) {
            loadCustomers()
        } else {
            searchCustomers(searchText)
        }
    }

    private fun loadCustomers() {

        lifecycleScope.launch {

            val database =
                AppDatabase.getDatabase(
                    applicationContext
                )

            val customers =
                database.customerDao()
                    .getAllCustomers()

            displayCustomersWithBalances(
                customers,
                database
            )
        }
    }

    private fun searchCustomers(
        searchText: String
    ) {

        lifecycleScope.launch {

            val database =
                AppDatabase.getDatabase(
                    applicationContext
                )

            val customers =
                if (searchText.isEmpty()) {

                    database.customerDao()
                        .getAllCustomers()

                } else {

                    database.customerDao()
                        .searchCustomers(
                            searchText
                        )
                }

            displayCustomersWithBalances(
                customers,
                database
            )
        }
    }

    private suspend fun displayCustomersWithBalances(
        customers: List<Customer>,
        database: AppDatabase
    ) {

        customerListContainer.removeAllViews()

        if (customers.isEmpty()) {
            showEmptyMessage()
            return
        }

        customers.forEach { customer ->

            val totalCredit =
                database.creditDao()
                    .getTotalCreditForCustomer(
                        customer.customerId
                    )

            val totalPaid =
                database.paymentDao()
                    .getTotalPaidForCustomer(
                        customer.customerId
                    )

            val outstanding =
                (totalCredit - totalPaid)
                    .coerceAtLeast(0.0)

            addCustomerCard(
                customer,
                outstanding
            )
        }
    }

    private fun showEmptyMessage() {

        val emptyText =
            TextView(this).apply {

                text = "No customers found"
                textSize = 16f
                gravity = Gravity.CENTER

                setTextColor(
                    android.graphics.Color.parseColor(
                        "#6B7280"
                    )
                )

                setPadding(
                    0,
                    dpToPx(40),
                    0,
                    0
                )
            }

        customerListContainer.addView(
            emptyText
        )
    }

    private fun addCustomerCard(
        customer: Customer,
        outstanding: Double
    ) {

        val card =
            MaterialCardView(this).apply {

                radius =
                    dpToPx(12).toFloat()

                cardElevation =
                    dpToPx(1).toFloat()

                setCardBackgroundColor(
                    android.graphics.Color.WHITE
                )

                strokeWidth =
                    dpToPx(1)

                strokeColor =
                    android.graphics.Color.parseColor(
                        "#E5E5E5"
                    )

                isClickable = true
                isFocusable = true

                layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dpToPx(86)
                    ).apply {

                        bottomMargin =
                            dpToPx(12)
                    }

                setOnClickListener {
                    openCustomerAction(customer)
                }
            }

        val row =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL

                setPadding(
                    dpToPx(16),
                    0,
                    dpToPx(12),
                    0
                )
            }

        val avatarCard =
            MaterialCardView(this).apply {

                radius =
                    dpToPx(24).toFloat()

                cardElevation = 0f

                setCardBackgroundColor(
                    android.graphics.Color.parseColor(
                        "#E5EEFF"
                    )
                )

                layoutParams =
                    LinearLayout.LayoutParams(
                        dpToPx(48),
                        dpToPx(48)
                    )
            }

        val initial =
            customer.name
                .trim()
                .firstOrNull()
                ?.uppercaseChar()
                ?.toString()
                ?: "?"

        val avatarText =
            TextView(this).apply {

                text = initial
                textSize = 20f
                gravity = Gravity.CENTER

                setTextColor(
                    android.graphics.Color.parseColor(
                        "#007565"
                    )
                )
            }

        avatarCard.addView(
            avatarText
        )

        val customerInfo =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                layoutParams =
                    LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    ).apply {

                        marginStart =
                            dpToPx(16)
                    }
            }

        val nameText =
            TextView(this).apply {

                text = customer.name
                textSize = 18f

                setTextColor(
                    android.graphics.Color.parseColor(
                        "#182033"
                    )
                )

                setTypeface(
                    typeface,
                    android.graphics.Typeface.BOLD
                )
            }

        val phoneText =
            TextView(this).apply {

                text = customer.phoneNumber
                textSize = 13f

                setTextColor(
                    android.graphics.Color.parseColor(
                        "#454C4B"
                    )
                )

                setPadding(
                    0,
                    dpToPx(4),
                    0,
                    0
                )
            }

        customerInfo.addView(
            nameText
        )

        customerInfo.addView(
            phoneText
        )

        val balanceArea =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                gravity =
                    Gravity.END
            }

        val outstandingLabel =
            TextView(this).apply {

                text = "Outstanding"
                textSize = 12f

                setTextColor(
                    android.graphics.Color.parseColor(
                        "#454C4B"
                    )
                )
            }

        val outstandingAmount =
            TextView(this).apply {

                text =
                    formatAmount(
                        outstanding
                    )

                textSize = 16f

                setTextColor(
                    android.graphics.Color.parseColor(
                        "#007565"
                    )
                )

                setTypeface(
                    typeface,
                    android.graphics.Typeface.BOLD
                )

                setPadding(
                    0,
                    dpToPx(4),
                    0,
                    0
                )
            }

        balanceArea.addView(
            outstandingLabel
        )

        balanceArea.addView(
            outstandingAmount
        )

        val deleteIcon =
            ImageView(this).apply {

                setImageResource(
                    R.drawable.ic_delete
                )

                contentDescription =
                    "Delete Customer"

                scaleType =
                    ImageView.ScaleType.CENTER_INSIDE

                isClickable = true
                isFocusable = true

                setPadding(
                    dpToPx(5),
                    dpToPx(5),
                    dpToPx(5),
                    dpToPx(5)
                )

                layoutParams =
                    LinearLayout.LayoutParams(
                        dpToPx(38),
                        dpToPx(38)
                    ).apply {

                        marginStart =
                            dpToPx(8)
                    }

                setOnClickListener {
                    showDeleteConfirmation(
                        customer
                    )
                }
            }

        row.addView(
            avatarCard
        )

        row.addView(
            customerInfo
        )

        row.addView(
            balanceArea
        )

        row.addView(
            deleteIcon
        )

        card.addView(
            row
        )

        customerListContainer.addView(
            card
        )
    }

    private fun showDeleteConfirmation(
        customer: Customer
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Customer")
            .setMessage(
                "Are you sure you want to delete ${customer.name}? All related credit and payment records will also be deleted."
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                deleteCustomer(
                    customer
                )
            }
            .show()
    }

    private fun deleteCustomer(
        customer: Customer
    ) {

        lifecycleScope.launch {

            val database =
                AppDatabase.getDatabase(
                    applicationContext
                )

            database.customerDao()
                .deleteCustomer(
                    customer
                )

            Toast.makeText(
                this@CustomerListActivity,
                "Customer deleted successfully",
                Toast.LENGTH_SHORT
            ).show()

            refreshCustomerList()
        }
    }

    private fun openCustomerAction(
        customer: Customer
    ) {

        val destinationIntent =
            when (customerAction) {

                "ADD_CREDIT" -> {

                    Intent(
                        this,
                        AddCreditActivity::class.java
                    )
                }

                "RECORD_PAYMENT" -> {

                    Intent(
                        this,
                        RecordPaymentActivity::class.java
                    )
                }

                else -> {

                    Intent(
                        this,
                        CustomerDetailsActivity::class.java
                    )
                }
            }

        destinationIntent.putExtra(
            "CUSTOMER_ID",
            customer.customerId
        )

        startActivity(
            destinationIntent
        )
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

    private fun dpToPx(
        dp: Int
    ): Int {

        return (
                dp *
                        resources.displayMetrics.density
                ).toInt()
    }
}