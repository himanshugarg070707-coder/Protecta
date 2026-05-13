package com.example.safeher.activities;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safeher.R;
import com.example.safeher.database.ContactsRepository;
import com.example.safeher.models.EmergencyContact;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class EmergencyContactsActivity extends AppCompatActivity {

    private ContactsRepository contactsRepository;
    private ContactsAdapter contactsAdapter;
    private TextView tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        contactsRepository = new ContactsRepository(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerContacts);
        tvEmptyState = findViewById(R.id.tvContactsEmpty);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddContact);

        contactsAdapter = new ContactsAdapter(new ContactsAdapter.ContactActionListener() {
            @Override
            public void onEdit(EmergencyContact contact) {
                openAddEditDialog(contact);
            }

            @Override
            public void onDelete(EmergencyContact contact) {
                confirmDelete(contact);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactsAdapter);

        fabAdd.setOnClickListener(view -> openAddEditDialog(null));

        loadContacts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts();
    }

    private void loadContacts() {
        List<EmergencyContact> contacts = contactsRepository.getAllContacts();
        contactsAdapter.updateContacts(contacts);

        tvEmptyState.setVisibility(contacts.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void openAddEditDialog(EmergencyContact existingContact) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_contact, null, false);

        EditText etName = dialogView.findViewById(R.id.etContactName);
        EditText etPhone = dialogView.findViewById(R.id.etContactPhone);

        if (existingContact != null) {
            etName.setText(existingContact.getName());
            etPhone.setText(existingContact.getPhoneNumber());
        }

        String title = existingContact == null ? "Add Emergency Contact" : "Edit Emergency Contact";
        String actionText = existingContact == null ? "Save" : "Update";

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton(actionText, null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (!isValidInput(etName, etPhone, name, phone)) {
                return;
            }

            boolean success = saveContact(existingContact, name, phone);
            if (success) {
                dialog.dismiss();
                loadContacts();
            }
        }));

        dialog.show();
    }

    private boolean isValidInput(EditText etName, EditText etPhone, String name, String phone) {
        etName.setError(null);
        etPhone.setError(null);

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone number is required");
            return false;
        }

        if (!Patterns.PHONE.matcher(phone).matches()) {
            etPhone.setError("Enter a valid phone number");
            return false;
        }

        return true;
    }

    private boolean saveContact(EmergencyContact existingContact, String name, String phone) {
        try {
            if (existingContact == null) {
                long result = contactsRepository.addContact(new EmergencyContact(name, phone));
                if (result < 0) {
                    Toast.makeText(this, "Could not save contact", Toast.LENGTH_SHORT).show();
                    return false;
                }
                Toast.makeText(this, "Contact saved", Toast.LENGTH_SHORT).show();
                return true;
            }

            existingContact.setName(name);
            existingContact.setPhoneNumber(phone);
            int updated = contactsRepository.updateContact(existingContact);
            if (updated <= 0) {
                Toast.makeText(this, "Could not update contact", Toast.LENGTH_SHORT).show();
                return false;
            }

            Toast.makeText(this, "Contact updated", Toast.LENGTH_SHORT).show();
            return true;

        } catch (SQLiteConstraintException ex) {
            Toast.makeText(this, "Phone number already exists", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void confirmDelete(EmergencyContact contact) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Contact")
                .setMessage("Delete " + contact.getName() + " from emergency contacts?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    contactsRepository.deleteContact(contact.getId());
                    Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show();
                    loadContacts();
                })
                .show();
    }
}
