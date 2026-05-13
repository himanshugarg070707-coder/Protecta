package com.example.safeher.models;

public class RouteSuggestion {
    private final String routeName;
    private final String status;
    private final String note;

    public RouteSuggestion(String routeName, String status, String note) {
        this.routeName = routeName;
        this.status = status;
        this.note = note;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }
}
