package com.example.nayapotha

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.nayapotha.data.AppDatabase
import com.example.nayapotha.data.Credit
import com.example.nayapotha.data.Payment
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

class TransactionHistoryActivity : AppCompatActivity() {

    private var customerId: Int = -1

    private lateinit var tvCustomerName: TextView
    private lateinit var tvCurrentOutstanding: TextView
    private lateinit var tvTransactionCount: TextView
    private lateinit var transactionListContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaction_history)

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

        tvCustomerName =
            findViewById(R.id.tvCustomerName)

        tvCurrentOutstanding =
            findViewById(R.id.tvCurrentOutstanding)

        tvTransactionCount =
            findViewById(R.id.tvTransactionCount)

        transactionListContainer =
            findViewById(R.id.transactionListContainer)

        btnBack.setOnClickListener {
            finish()
        }

        loadTransactionHistory()
    }

    override fun onResume() {
        super.onResume()

        if (customerId != -1) {
            loadTransactionHistory()
        }
    }

    private fun loadTransactionHistory() {

        lifecycleScope.launch {

            val database =
                AppDatabase.getDatabase(applicationContext)

            val customer =
                database.customerDao()
                    .getCustomerById(customerId)

            if (customer == null) {
                Toast.makeText(
                    this@TransactionHistoryActivity,
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

            val outstanding =
                (totalCredit - totalPaid)
                    .coerceAtLeast(0.0)

            tvCustomerName.text =
                customer.name

            tvCurrentOutstanding.text =
                formatAmount(outstanding)

            val transactionCount =
                credits.size + payments.size

            tvTransactionCount.text =
                if (transactionCount == 1) {
                    "1 transaction"
                } else {
                    "$transactionCount transactions"
                }

            transactionListContainer.removeAllViews()

            if (transactionCount == 0) {

                val emptyMessage =
                    TextView(this@TransactionHistoryActivity).apply {

                        text = "No transactions yet"
                        textSize = 15f
                        gravity = Gravity.CENTER

                        setTextColor(
                            Color.parseColor("#7A8280")
                        )

                        layoutParams =
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                dpToPx(100)
                            )
                    }

                transactionListContainer.addView(
                    emptyMessage
                )

                return@launch
            }

            credits.forEach { credit ->
                addCreditCard(credit)
            }

            payments.forEach { payment ->
                addPaymentCard(payment)
            }
        }
    }

    private fun addCreditCard(
        credit: Credit
    ) {

        val clickAction = View.OnClickListener {
            showDeleteCreditConfirmation(credit)
        }

        val card =
            createTransactionCard(
                title = credit.description,
                date = credit.date,
                type = "Credit Added",
                amountText = "+ ${formatAmount(credit.amount)}",
                isCredit = true,
                clickAction = clickAction
            )

        transactionListContainer.addView(card)
    }

    private fun addPaymentCard(
        payment: Payment
    ) {

        val title =
            if (payment.note.isBlank()) {
                "Payment Received"
            } else {
                payment.note
            }

        val clickAction = View.OnClickListener {
            showDeletePaymentConfirmation(payment)
        }

        val card =
            createTransactionCard(
                title = title,
                date = payment.date,
                type = "Payment",
                amountText = "- ${formatAmount(payment.amount)}",
                isCredit = false,
                clickAction = clickAction
            )

        transactionListContainer.addView(card)
    }

    private fun createTransactionCard(
        title: String,
        date: String,
        type: String,
        amountText: String,
        isCredit: Boolean,
        clickAction: View.OnClickListener
    ): MaterialCardView {

        val card =
            MaterialCardView(this).apply {

                radius = dpToPx(12).toFloat()
                cardElevation = 0f

                setCardBackgroundColor(
                    Color.WHITE
                )

                strokeWidth = dpToPx(1)

                strokeColor =
                    Color.parseColor("#E5E7EB")

                isClickable = true
                isFocusable = true

                setOnClickListener(clickAction)

                layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dpToPx(82)
                    ).apply {

                        bottomMargin =
                            dpToPx(10)
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
                    dpToPx(16),
                    0
                )

                isClickable = true
                isFocusable = true

                setOnClickListener(clickAction)
            }

        val leftArea =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                layoutParams =
                    LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )

                isClickable = true
                setOnClickListener(clickAction)
            }

        val titleText =
            TextView(this).apply {

                text = title
                textSize = 16f

                setTextColor(
                    Color.parseColor("#182033")
                )

                setTypeface(
                    typeface,
                    android.graphics.Typeface.BOLD
                )

                isClickable = true
                setOnClickListener(clickAction)
            }

        val dateText =
            TextView(this).apply {

                text = date
                textSize = 12f

                setTextColor(
                    Color.parseColor("#7A8280")
                )

                setPadding(
                    0,
                    dpToPx(5),
                    0,
                    0
                )

                isClickable = true
                setOnClickListener(clickAction)
            }

        leftArea.addView(titleText)
        leftArea.addView(dateText)

        val rightArea =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                gravity =
                    Gravity.END

                isClickable = true
                setOnClickListener(clickAction)
            }

        val amountTextView =
            TextView(this).apply {

                text = amountText
                textSize = 16f

                setTextColor(
                    if (isCredit) {
                        Color.parseColor("#D65A45")
                    } else {
                        Color.parseColor("#007565")
                    }
                )

                setTypeface(
                    typeface,
                    android.graphics.Typeface.BOLD
                )

                isClickable = true
                setOnClickListener(clickAction)
            }

        val typeText =
            TextView(this).apply {

                text = type
                textSize = 12f

                setTextColor(
                    Color.parseColor("#7A8280")
                )

                setPadding(
                    0,
                    dpToPx(5),
                    0,
                    0
                )

                isClickable = true
                setOnClickListener(clickAction)
            }

        rightArea.addView(amountTextView)
        rightArea.addView(typeText)

        row.addView(leftArea)
        row.addView(rightArea)

        card.addView(row)

        return card
    }

    private fun showDeleteCreditConfirmation(
        credit: Credit
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Credit")
            .setMessage(
                "Are you sure you want to delete this credit record?"
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                deleteCredit(credit)
            }
            .show()
    }

    private fun showDeletePaymentConfirmation(
        payment: Payment
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Payment")
            .setMessage(
                "Are you sure you want to delete this payment record?"
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                deletePayment(payment)
            }
            .show()
    }

    private fun deleteCredit(
        credit: Credit
    ) {

        lifecycleScope.launch {

            val database =
                AppDatabase.getDatabase(applicationContext)

            database.creditDao()
                .deleteCredit(credit)

            Toast.makeText(
                this@TransactionHistoryActivity,
                "Credit deleted successfully",
                Toast.LENGTH_SHORT
            ).show()

            loadTransactionHistory()
        }
    }

    private fun deletePayment(
        payment: Payment
    ) {

        lifecycleScope.launch {

            val database =
                AppDatabase.getDatabase(applicationContext)

            database.paymentDao()
                .deletePayment(payment)

            Toast.makeText(
                this@TransactionHistoryActivity,
                "Payment deleted successfully",
                Toast.LENGTH_SHORT
            ).show()

            loadTransactionHistory()
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

    private fun dpToPx(
        dp: Int
    ): Int {

        return (
                dp *
                        resources.displayMetrics.density
                ).toInt()
    }
}