package com.example.myweighttrackingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// This class is the adapter for the RecyclerView that shows weight entries
public class WeightData extends RecyclerView.Adapter<WeightData.WeightViewHolder> {

    // List of weight entries to display
    private List<WeightEntry> weightList;

    // Database helper to access database functions
    private DatabaseHelper dbHelper;

    // Constructor that sets the list and database helper
    public WeightData(List<WeightEntry> weightList, DatabaseHelper dbHelper) {
        this.weightList = weightList;
        this.dbHelper = dbHelper;
    }

    // ViewHolder class holds the views for each item in the list
    public static class WeightViewHolder extends RecyclerView.ViewHolder {
        TextView textDate, textWeight;
        Button buttonDelete;
        Button buttonEdit;

        // Link each view in the item layout to a variable
        public WeightViewHolder(View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.textDate);
            textWeight = itemView.findViewById(R.id.textWeight);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
        }
    }

    // Called when the RecyclerView needs a new view holder
    @NonNull
    @Override
    public WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weight, parent, false);
        return new WeightViewHolder(view);
    }

    // Called to display data at a specific position in the list
    @Override
    public void onBindViewHolder(@NonNull WeightViewHolder holder, int position) {
        // Get the weight entry at this position
        WeightEntry entry = weightList.get(position);

        // Show the date and weight in the TextViews
        holder.textDate.setText("Date: " + entry.getDate());
        holder.textWeight.setText("Weight: " + entry.getWeight() + " lbs");

        // Handle delete button click
        holder.buttonDelete.setOnClickListener(v -> {
            boolean deleted = dbHelper.deleteWeight(entry.getId());
            if (deleted) {
                weightList.remove(position);              // Remove from list
                notifyItemRemoved(position);             // Update RecyclerView
                Toast.makeText(v.getContext(), "Entry deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(v.getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle edit button click
        holder.buttonEdit.setOnClickListener(v -> {
            // Create a popup dialog to edit date and weight
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(v.getContext());
            View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.edit_entry, null);
            builder.setView(dialogView);

            // Get input fields inside the dialog
            EditText editDate = dialogView.findViewById(R.id.editDateDialog);
            EditText editWeight = dialogView.findViewById(R.id.editWeightDialog);

            // Set current values
            editDate.setText(entry.getDate());
            editWeight.setText(String.valueOf(entry.getWeight()));

            // Set up Update and Cancel buttons
            builder.setTitle("Edit Entry")
                    .setPositiveButton("Update", (dialog, which) -> {
                        String newDate = editDate.getText().toString().trim();
                        String newWeightStr = editWeight.getText().toString().trim();

                        try {
                            double newWeight = Double.parseDouble(newWeightStr);
                            boolean updated = dbHelper.updateWeight(entry.getId(), newDate, newWeight);
                            if (updated) {
                                // Update the list and refresh display
                                entry.setDate(newDate);
                                entry.setWeight(newWeight);
                                notifyItemChanged(position);
                                Toast.makeText(v.getContext(), "Entry updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(v.getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(v.getContext(), "Invalid weight input", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        });
    }

    // Returns the total number of items in the list
    @Override
    public int getItemCount() {
        return weightList.size();
    }
}