package com.example.safeher.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.telephony.SmsManager;

import androidx.core.content.ContextCompat;

import com.example.safeher.database.ContactsRepository;
import com.example.safeher.models.EmergencyContact;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class SOSManager {

    public interface SosCallback {
        void onSuccess(int sentCount);

        void onFailure(String reason);
    }

    private SOSManager() {
    }

    public static void sendSosToAllContacts(Context context, String reason, SosCallback callback) {
        ContactsRepository repository = new ContactsRepository(context);
        List<EmergencyContact> contacts = repository.getAllContacts();

        if (contacts.isEmpty()) {
            callback.onFailure("No emergency contacts found. Add at least one contact first.");
            return;
        }

        if (!PermissionUtils.hasSmsPermission(context)) {
            callback.onFailure("SMS permission is missing.");
            return;
        }

        fetchLocationAndSend(context, contacts, reason, callback);
    }

    @SuppressLint("MissingPermission")
    private static void fetchLocationAndSend(
            Context context,
            List<EmergencyContact> contacts,
            String reason,
            SosCallback callback
    ) {
        boolean hasLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        if (!hasLocation) {
            sendSms(context, contacts, reason, null, callback);
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> sendSms(context, contacts, reason, location, callback))
                .addOnFailureListener(error -> sendSms(context, contacts, reason, null, callback));
    }

    private static void sendSms(
            Context context,
            List<EmergencyContact> contacts,
            String reason,
            Location location,
            SosCallback callback
    ) {
        String message = buildMessage(reason, location);
        SmsManager smsManager = SmsManager.getDefault();

        int sentCount = 0;
        List<String> failedContacts = new ArrayList<>();

        for (EmergencyContact contact : contacts) {
            try {
                ArrayList<String> messageParts = smsManager.divideMessage(message);
                smsManager.sendMultipartTextMessage(contact.getPhoneNumber(), null, messageParts, null, null);
                sentCount++;
            } catch (Exception ex) {
                failedContacts.add(contact.getName() + " (" + contact.getPhoneNumber() + ")");
            }
        }

        if (sentCount > 0) {
            callback.onSuccess(sentCount);
        } else {
            callback.onFailure("Failed to send SMS to all contacts: " + failedContacts);
        }
    }

    private static String buildMessage(String reason, Location location) {
        StringBuilder builder = new StringBuilder();
        builder.append("PROTECTA SOS ALERT!\n");
        builder.append("Reason: ").append(reason).append("\n");
        builder.append("I need immediate help.\n");

        if (location != null) {
            builder.append("My location: https://maps.google.com/?q=")
                    .append(location.getLatitude())
                    .append(",")
                    .append(location.getLongitude())
                    .append("\n");
        } else {
            builder.append("Location is currently unavailable.\n");
        }

        builder.append("Sent from Protecta App.");
        return builder.toString();
    }
}
