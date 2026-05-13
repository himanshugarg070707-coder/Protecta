package com.example.safeher.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.safeher.models.EmergencyContact;

import java.util.ArrayList;
import java.util.List;

public class ContactsRepository {

    private final ContactsDatabaseHelper dbHelper;

    public ContactsRepository(Context context) {
        this.dbHelper = new ContactsDatabaseHelper(context);
    }

    public long addContact(EmergencyContact contact) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ContactsDatabaseHelper.COLUMN_NAME, contact.getName());
        values.put(ContactsDatabaseHelper.COLUMN_PHONE, contact.getPhoneNumber());
        return db.insert(ContactsDatabaseHelper.TABLE_CONTACTS, null, values);
    }

    public int updateContact(EmergencyContact contact) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ContactsDatabaseHelper.COLUMN_NAME, contact.getName());
        values.put(ContactsDatabaseHelper.COLUMN_PHONE, contact.getPhoneNumber());
        return db.update(
                ContactsDatabaseHelper.TABLE_CONTACTS,
                values,
                ContactsDatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(contact.getId())}
        );
    }

    public int deleteContact(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                ContactsDatabaseHelper.TABLE_CONTACTS,
                ContactsDatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public List<EmergencyContact> getAllContacts() {
        List<EmergencyContact> contacts = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                ContactsDatabaseHelper.TABLE_CONTACTS,
                null,
                null,
                null,
                null,
                null,
                ContactsDatabaseHelper.COLUMN_NAME + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsDatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsDatabaseHelper.COLUMN_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsDatabaseHelper.COLUMN_PHONE));
                contacts.add(new EmergencyContact(id, name, phone));
            }
            cursor.close();
        }

        return contacts;
    }
}
