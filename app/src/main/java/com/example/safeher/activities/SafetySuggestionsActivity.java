package com.example.safeher.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safeher.R;
import com.example.safeher.models.RouteSuggestion;

import java.util.ArrayList;
import java.util.List;

public class SafetySuggestionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_suggestions);

        RecyclerView recyclerRoutes = findViewById(R.id.recyclerRoutes);
        recyclerRoutes.setLayoutManager(new LinearLayoutManager(this));

        RouteSuggestionAdapter adapter = new RouteSuggestionAdapter();
        recyclerRoutes.setAdapter(adapter);

        adapter.setSuggestions(getDummySuggestions());
    }

    private List<RouteSuggestion> getDummySuggestions() {
        List<RouteSuggestion> suggestions = new ArrayList<>();
        suggestions.add(new RouteSuggestion(
                "Campus Main Gate -> Girls Hostel",
                "SAFE",
                "Well-lit route, nearby security booth, CCTV coverage."
        ));
        suggestions.add(new RouteSuggestion(
                "Library Back Road -> Hostel",
                "UNSAFE",
                "Low street lighting and low foot traffic after 8 PM."
        ));
        suggestions.add(new RouteSuggestion(
                "Cafeteria Lane -> Metro Stop",
                "SAFE",
                "Busy route with shops and police patrol points."
        ));
        suggestions.add(new RouteSuggestion(
                "Parking Lot Shortcut -> Hostel",
                "UNSAFE",
                "Sparse movement and blind turns; avoid at night."
        ));
        return suggestions;
    }
}
