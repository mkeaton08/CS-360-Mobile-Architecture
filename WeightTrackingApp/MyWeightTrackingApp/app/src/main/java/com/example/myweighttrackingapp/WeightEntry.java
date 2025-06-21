package com.example.myweighttrackingapp;

public class WeightEntry {
    private int id;
    private String username;
    private String date;
    private double weight;

    public WeightEntry(int id, String username, String date, double weight) {
        this.id = id;
        this.username = username;
        this.date = date;
        this.weight = weight;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public double getWeight() {
        return weight;
    }

    // Setters
    public void setDate(String date) {
        this.date = date;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
