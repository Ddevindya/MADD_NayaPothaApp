# NayaPotha

NayaPotha is a Kotlin-based Android application developed for small shop owners to manage customer credit and payment records digitally.

The application is designed as a simple replacement for handwritten credit books and focuses on customer-based credit tracking, payment recording, balance calculation, and transaction history.

## Main Features

- Add, view, edit, and delete customers
- Search customers by name or phone number
- Add multiple credit records for the same customer
- Record full or partial payments
- Automatically calculate remaining customer balances
- View customer transaction history
- Delete individual credit or payment records
- Dashboard with total outstanding, customer count, and payments received
- Quick actions for adding customers, credit, and payments
- Offline local data storage using Room Database
- Input validation and delete confirmation dialogs
- Settings and information screens

## Technology Stack

- Kotlin
- Android Studio
- XML Layouts
- Room Database
- Android Jetpack
- Material Components
- Coroutines

## Database Structure

The application uses three main Room entities:

- Customer
- Credit
- Payment

A customer can have multiple credit and payment records. Related credit and payment records are automatically removed when a customer is deleted.

## Core Calculation

Remaining Balance = Total Credit - Total Payments

Example:

- Total Credit: Rs. 8,500
- Total Paid: Rs. 1,000
- Remaining Balance: Rs. 7,500

## Application Flow

Splash Screen  
→ Welcome Screen  
→ Dashboard  
→ Customers  
→ Customer Details  
→ Add Credit / Record Payment  
→ Transaction History

## Development Information

This application was developed as part of the Mobile Application Design and Development module.

The project focuses on providing a simple, offline, and user-friendly credit management solution for small shop owners.

## Repository

This repository contains the Android Studio project source code for NayaPotha.
