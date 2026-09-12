package com.example.nayapotha

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.nayapotha.data.AppDatabase
import com.example.nayapotha.data.Payment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RecordPaymentActivity : AppCompatActivity() {

    private var customerId: Int = -1
    private var currentBalance: Double = 0.0
    private var customerName: String = ""

    private lateinit var etPaymentAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var etNote: EditText

    private lateinit var tvCustomerInitial: TextView
    private lateinit var tvCustomerName: TextView
    private lateinit var tvCurrentBalance: TextView
    private lateinit var tvCalculationCurrentBalance: TextView
    private lateinit var tvPaymentAmount: TextView
    private lateinit var tvRemainingBalance: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_record_payment)

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

        val btnCancel =
            findViewById<TextView>(R.id.btnCancel)

        val btnConfirmPayment =
            findViewById<MaterialButton>(R.id.btnConfirmPayment)

        etPaymentAmount =
            findViewById(R.id.etPaymentAmount)

        etDate =
            findViewById(R.id.etDate)

        etNote =
            findViewById(R.id.etNote)

        tvCustomerInitial =
            findViewById(R.id.tvCustomerInitial)

        tvCustomerName =
            findViewById(R.id.tvCustomerName)

        tvCurrentBalance =
            findViewById(R.id.tvCurrentBalance)

        tvCalculationCurrentBalance =
            findViewById(R.id.tvCalculationCurrentBalance)

        tvPaymentAmount =
            findViewById(R.id.tvPaymentAmount)

        tvRemainingBalance =
            findViewById(R.id.tvRemainingBalance)

        btnBack.setOnClickListener {
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        etDate.setOnClickListener {
            showDatePicker()
        }

        etPaymentAmount.addTextChangedListener(
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
                    updateLiveCalculation()
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )

        btnConfirmPayment.setOnClickListener {
            savePayment()
        }

        setTodayDate()
        loadCustomerData()
    }

    private fun loadCustomerData() {

        lifecycleScope.launch {

            val database =
                AppDatabase.getDatabase(applicationContext)

            val customer =
                database.customerDao()
                    .getCustomerById(customerId)

            if (customer == null) {
                Toast.makeText(
                    this@RecordPaymentActivity,
                    "Customer not found",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
                return@launch
            }

            val totalCredit =
                database.creditDao()
                    .getTotalCreditForCustomer(customerId)

            val totalPaid =
                database.paymentDao()
                    .getTotalPaidForCustomer(customerId)

            currentBalance =
                (totalCredit - totalPaid)
                    .coerceAtLeast(0.0)

            customerName =
                customer.name

            tvCustomerName.text =
                customer.name

            tvCustomerInitial.text =
                getInitials(customer.name)

            tvCurrentBalance.text =
                formatAmount(currentBalance)

            tvCalculationCurrentBalance.text =
                formatAmount(currentBalance)

            updateLiveCalculation()
        }
    }

    private fun updateLiveCalculation() {

        val paymentAmount =
            etPaymentAmount.text
                .toString()
                .trim()
                .toDoubleOrNull()
                ?: 0.0

        val remainingBalance =
            (currentBalance - paymentAmount)
                .coerceAtLeast(0.0)

        tvPaymentAmount.text =
            "- ${formatAmount(paymentAmount)}"

        tvRemainingBalance.text =
            formatAmount(remainingBalance)
    }

    private fun savePayment() {

        val amountText =
            etPaymentAmount.text
                .toString()
                .trim()

        val date =
            etDate.text
                .toString()
                .trim()

        val note =
            etNote.text
                .toString()
                .trim()

        if (amountText.isEmpty()) {
            etPaymentAmount.error =
                "Please enter payment amount"

            etPaymentAmount.requestFocus()
            return
        }

        val amount =
            amountText.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            etPaymentAmount.error =
                "Please enter a valid amount"

            etPaymentAmount.requestFocus()
            return
        }

        if (currentBalance <= 0) {
            Toast.makeText(
                this,
                "This customer has no outstanding balance",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (amount > currentBalance) {
            etPaymentAmount.error =
                "Payment cannot exceed the remaining balance"

            etPaymentAmount.requestFocus()
            return
        }

        if (date.isEmpty()) {
            Toast.makeText(
                this,
                "Please select payment date",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val previousBalance =
            currentBalance

        val remainingBalance =
            previousBalance - amount

        val payment =
            Payment(
                customerId = customerId,
                amount = amount,
                date = date,
                note = note
            )

        lifecycleScope.launch {

            val database =
                AppDatabase.getDatabase(applicationContext)

            database.paymentDao()
                .insertPayment(payment)

            val intent =
                Intent(
                    this@RecordPaymentActivity,
                    PaymentSuccessActivity::class.java
                )

            intent.putExtra(
                "CUSTOMER_ID",
                customerId
            )

            intent.putExtra(
                "CUSTOMER_NAME",
                customerName
            )

            intent.putExtra(
                "PAYMENT_AMOUNT",
                amount
            )

            intent.putExtra(
                "PREVIOUS_BALANCE",
                previousBalance
            )

            intent.putExtra(
                "REMAINING_BALANCE",
                remainingBalance
            )

            startActivity(intent)

            finish()
        }
    }

    private fun showDatePicker() {

        val calendar =
            Calendar.getInstance()

        val dialog =
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->

                    val selectedCalendar =
                        Calendar.getInstance()

                    selectedCalendar.set(
                        year,
                        month,
                        dayOfMonth
                    )

                    val formatter =
                        SimpleDateFormat(
                            "dd MMM yyyy",
                            Locale.getDefault()
                        )

                    etDate.setText(
                        formatter.format(
                            selectedCalendar.time
                        )
                    )
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

        dialog.show()
    }

    private fun setTodayDate() {

        val formatter =
            SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            )

        etDate.setText(
            formatter.format(
                Calendar.getInstance().time
            )
        )
    }

    private fun getInitials(
        name: String
    ): String {

        val parts =
            name.trim()
                .split(" ")
                .filter {
                    it.isNotBlank()
                }

        return when {
            parts.isEmpty() ->
                "?"

            parts.size == 1 ->
                parts[0]
                    .take(1)
                    .uppercase()

            else ->
                (
                        parts.first().take(1) +
                                parts.last().take(1)
                        ).uppercase()
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