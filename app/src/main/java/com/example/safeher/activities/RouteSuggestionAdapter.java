package com.example.safeher.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safeher.R;
import com.example.safeher.models.RouteSuggestion;

import java.util.ArrayList;
import java.util.List;

public class RouteSuggestionAdapter extends RecyclerView.Adapter<RouteSuggestionAdapter.RouteViewHolder> {

    private final List<RouteSuggestion> suggestions = new ArrayList<>();

    public void setSuggestions(List<RouteSuggestion> data) {
        suggestions.clear();
        suggestions.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route_suggestion, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        RouteSuggestion suggestion = suggestions.get(position);
        holder.tvRouteName.setText(suggestion.getRouteName());
        holder.tvNote.setText(suggestion.getNote());

        String status = suggestion.getStatus().toUpperCase();
        holder.tvStatus.setText(status);
        if ("SAFE".equals(status)) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_safe);
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_unsafe);
        }
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    static class RouteViewHolder extends RecyclerView.ViewHolder {
        TextView tvRouteName;
        TextView tvStatus;
        TextView tvNote;

        RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRouteName = itemView.findViewById(R.id.tvRouteName);
            tvStatus = itemView.findViewById(R.id.tvRouteStatus);
            tvNote = itemView.findViewById(R.id.tvRouteNote);
        }
    }
}
