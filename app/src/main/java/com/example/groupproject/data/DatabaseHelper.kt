package com.example.groupproject.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.Cursor

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "unitefood.db"
        const val DATABASE_VERSION = 1

        // Table
        const val TABLE_FOOD = "food_items"

        // Columns
        const val COL_ID = "id"
        const val COL_DONOR_ID = "donor_id"
        const val COL_NAME = "food_name"
        const val COL_CATEGORY = "category"
        const val COL_QUANTITY = "quantity"
        const val COL_UNIT = "unit"
        const val COL_DESCRIPTION = "description"
        const val COL_PICKUP_AREA = "pickup_area"
        const val COL_PICKUP_ADDRESS = "pickup_address"
        const val COL_EXPIRY_DATE = "expiry_date"
        const val COL_STATUS = "status"
        const val COL_RESERVED_BY = "reserved_by"
        const val COL_COLLECTOR_ID = "collector_id"
        const val COL_PICKUP_TIME = "pickup_time"
        const val COL_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_FOOD (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DONOR_ID TEXT,
                $COL_NAME TEXT,
                $COL_CATEGORY TEXT,
                $COL_QUANTITY INTEGER,
                $COL_UNIT TEXT,
                $COL_DESCRIPTION TEXT,
                $COL_PICKUP_AREA TEXT,
                $COL_PICKUP_ADDRESS TEXT,
                $COL_EXPIRY_DATE TEXT,
                $COL_STATUS TEXT,
                $COL_RESERVED_BY TEXT,
                $COL_COLLECTOR_ID TEXT,
                $COL_PICKUP_TIME TEXT,
                $COL_CREATED_AT TEXT
            )
        """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FOOD")
        onCreate(db)
    }

    // Insert new food item — returns new row ID (-1 = fail)
    fun insertFood(
        donorId: String,
        name: String,
        category: String,
        quantity: Int,
        unit: String,
        description: String,
        pickupArea: String,
        pickupAddress: String,
        expiryDate: String,
        createdAt: String
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_DONOR_ID, donorId)
            put(COL_NAME, name)
            put(COL_CATEGORY, category)
            put(COL_QUANTITY, quantity)
            put(COL_UNIT, unit)
            put(COL_DESCRIPTION, description)
            put(COL_PICKUP_AREA, pickupArea)
            put(COL_PICKUP_ADDRESS, pickupAddress)
            put(COL_EXPIRY_DATE, expiryDate)
            put(COL_STATUS, "Available")
            put(COL_CREATED_AT, createdAt)
        }
        return db.insert(TABLE_FOOD, null, values)
    }

    // Search / filter food items
    fun searchFood(
        donorId: String,
        category: String,
        area: String,
        status: String
    ): Cursor {
        val db = readableDatabase
        val where = StringBuilder("$COL_DONOR_ID = ?")
        val args = mutableListOf(donorId)

        if (category != "All" && category.isNotEmpty()) {
            where.append(" AND $COL_CATEGORY = ?")
            args.add(category)
        }
        if (area.isNotEmpty()) {
            where.append(" AND $COL_PICKUP_AREA LIKE ?")
            args.add("%$area%")
        }
        if (status != "All" && status.isNotEmpty()) {
            where.append(" AND $COL_STATUS = ?")
            args.add(status)
        }

        return db.query(
            TABLE_FOOD,
            null,
            where.toString(),
            args.toTypedArray(),
            null, null,
            "$COL_CREATED_AT DESC"
        )
    }

    // Get single food item by ID
    fun getFoodById(id: Int): Cursor {
        val db = readableDatabase
        return db.query(
            TABLE_FOOD,
            null,
            "$COL_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )
    }

    // Update existing food item
    fun updateFood(
        id: Int,
        name: String,
        category: String,
        quantity: Int,
        unit: String,
        description: String,
        pickupArea: String,
        pickupAddress: String,
        expiryDate: String
    ): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NAME, name)
            put(COL_CATEGORY, category)
            put(COL_QUANTITY, quantity)
            put(COL_UNIT, unit)
            put(COL_DESCRIPTION, description)
            put(COL_PICKUP_AREA, pickupArea)
            put(COL_PICKUP_ADDRESS, pickupAddress)
            put(COL_EXPIRY_DATE, expiryDate)
        }
        return db.update(
            TABLE_FOOD,
            values,
            "$COL_ID = ?",
            arrayOf(id.toString())
        )
    }

    // Delete food item
    fun deleteFood(id: Int): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_FOOD,
            "$COL_ID = ?",
            arrayOf(id.toString())
        )
    }

    // Get reserved foods for donor
    fun getReservedFoods(donorId: String): Cursor {
        val db = readableDatabase
        return db.query(
            TABLE_FOOD,
            null,
            "$COL_DONOR_ID = ? AND $COL_STATUS = ?",
            arrayOf(donorId, "Reserved"),
            null, null,
            "$COL_CREATED_AT DESC"
        )
    }

    // Schedule pickup — set pickup_time + change status
    fun schedulePickup(id: Int, pickupTime: String, newStatus: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_PICKUP_TIME, pickupTime)
            put(COL_STATUS, newStatus)
        }
        return db.update(
            TABLE_FOOD,
            values,
            "$COL_ID = ?",
            arrayOf(id.toString())
        )
    }

    // For testing — reserve an item manually
    fun reserveForTest(id: Int, reservedBy: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_STATUS, "Reserved")
            put(COL_RESERVED_BY, reservedBy)
        }
        return db.update(
            TABLE_FOOD,
            values,
            "$COL_ID = ?",
            arrayOf(id.toString())
        )
    }

    // Get history — Reserved + Collected + Expired
    fun getDonorHistory(donorId: String): Cursor {
        val db = readableDatabase
        return db.query(
            TABLE_FOOD,
            null,
            "$COL_DONOR_ID = ? AND $COL_STATUS IN (?, ?, ?)",
            arrayOf(donorId, "Reserved", "Collected", "Expired"),
            null, null,
            "$COL_CREATED_AT DESC"
        )
    }
}
