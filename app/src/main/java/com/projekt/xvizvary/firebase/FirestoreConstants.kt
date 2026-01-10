package com.projekt.xvizvary.firebase

/**
 * Firestore collection and field constants
 */
object FirestoreConstants {
    // Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_TRANSACTIONS = "transactions"
    const val COLLECTION_CATEGORIES = "categories"
    const val COLLECTION_LIMITS = "limits"
    const val COLLECTION_RECEIPTS = "receipts"

    // User document fields
    const val FIELD_EMAIL = "email"
    const val FIELD_DISPLAY_NAME = "displayName"
    const val FIELD_CREATED_AT = "createdAt"

    // Transaction fields
    const val FIELD_NAME = "name"
    const val FIELD_AMOUNT = "amount"
    const val FIELD_TYPE = "type"
    const val FIELD_CATEGORY_ID = "categoryId"
    const val FIELD_DATE = "date"
    const val FIELD_NOTE = "note"

    // Category fields
    const val FIELD_ICON = "icon"
    const val FIELD_COLOR = "color"
    const val FIELD_IS_DEFAULT = "isDefault"

    // Limit fields
    const val FIELD_LIMIT_AMOUNT = "limitAmount"
    const val FIELD_PERIOD_MONTHS = "periodMonths"

    // Receipt fields
    const val FIELD_STORE_NAME = "storeName"
    const val FIELD_TOTAL_AMOUNT = "totalAmount"
    const val FIELD_RAW_TEXT = "rawText"
    const val FIELD_TRANSACTION_ID = "transactionId"
}
