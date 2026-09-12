package com.example.nayapotha

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

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

        val navHome =
            findViewById<LinearLayout>(R.id.navHome)

        val navCustomers =
            findViewById<LinearLayout>(R.id.navCustomers)

        val cardAppInformation =
            findViewById<LinearLayout>(R.id.cardAppInformation)

        val cardAboutNayaPotha =
            findViewById<LinearLayout>(R.id.cardAboutNayaPotha)

        val cardPrivacyPolicy =
            findViewById<LinearLayout>(R.id.cardPrivacyPolicy)

        val cardHelpSupport =
            findViewById<LinearLayout>(R.id.cardHelpSupport)

        navHome.setOnClickListener {
            val intent = Intent(
                this,
                DashboardActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(intent)
            finish()
        }

        navCustomers.setOnClickListener {
            val intent = Intent(
                this,
                CustomerListActivity::class.java
            )

            startActivity(intent)
            finish()
        }

        cardAppInformation.setOnClickListener {
            showInformationDialog(
                title = "App Information",
                message = "NayaPotha\nVersion 1.0\n\nOffline customer credit and payment tracking application for small shop owners."
            )
        }

        cardAboutNayaPotha.setOnClickListener {
            showInformationDialog(
                title = "About NayaPotha",
                message = "NayaPotha helps small shop owners replace handwritten credit books with a simple digital system for managing customers, credits, payments, balances, and transaction history."
            )
        }

        cardPrivacyPolicy.setOnClickListener {
            showInformationDialog(
                title = "Privacy Policy",
                message = "NayaPotha stores customer, credit, and payment information locally on this device. The application does not require cloud synchronization or online account access."
            )
        }

        cardHelpSupport.setOnClickListener {
            showInformationDialog(
                title = "Help & Support",
                message = "Use the Customers section to add or manage customers. Open a customer to add credit, record payments, edit details, and review transaction history."
            )
        }
    }

    private fun showInformationDialog(
        title: String,
        message: String
    ) {

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}