package com.android_basic.fecontactapp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.android_basic.fecontactapp.model.Contact

/***
 * Created by HoangRyan aka LilDua on 10/12/2023.
 */
class DatabaseHelper(context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object {
        private const val DATABASE_NAME = "contacts.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_CONTACTS = "contacts"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_IMAGE = "image"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create your tables here
        db?.execSQL("CREATE TABLE $TABLE_CONTACTS ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $COLUMN_NAME TEXT, $COLUMN_PHONE TEXT, $COLUMN_EMAIL TEXT, $COLUMN_IMAGE TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Handle database upgrade if needed
    }

    fun addContact(contact: Contact) {
        val values = ContentValues().apply {
            put(COLUMN_NAME, contact.name)
            put(COLUMN_PHONE,contact.phone)
            put(COLUMN_EMAIL,contact.email)
            put(COLUMN_IMAGE,contact.image)
        }

        val db = writableDatabase
        db.insert(TABLE_CONTACTS, null, values)
        db.close()
    }

    fun getAllContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val query = "SELECT * FROM $TABLE_CONTACTS"

        val db = readableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(with(cursor) {getColumnIndex(COLUMN_ID)})
                val name = cursor.getString(with(cursor) { getColumnIndex(COLUMN_NAME) })
                val phone = cursor.getString(with(cursor) {getColumnIndex(COLUMN_PHONE)})
                val email = cursor.getString(with(cursor) {getColumnIndex(COLUMN_EMAIL)})
                val imageBitmap = cursor.getString(with(cursor) {getColumnIndex(COLUMN_IMAGE)})
                val image = imageBitmap.toString()

                val contact = Contact(id = id, name = name,phone = phone,email = email,image = image)
                contacts.add(contact)
                for (contact: Contact in contacts){
                    Log.i("DatabaseHelper", "getAllContacts: Successfully ${contact.id}, ${contact.name}")

                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return contacts
    }

    fun updateContact(contact: Contact) {
        val values = ContentValues().apply {
            put(COLUMN_NAME, contact.name)
            put(COLUMN_PHONE, contact.phone)
            put(COLUMN_EMAIL, contact.email)
            put(COLUMN_IMAGE, contact.image)
        }

        val db = writableDatabase
        db.update(TABLE_CONTACTS, values, "$COLUMN_ID=?", arrayOf(contact.id.toString()))
        db.close()
    }

    fun deleteContact(contactId: Long) {
        val db = writableDatabase
        db.delete(TABLE_CONTACTS, "$COLUMN_ID=?", arrayOf(contactId.toString()))
        db.close()
    }


}