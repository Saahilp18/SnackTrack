package com.example.foodlogger;

public class foodItem {
    private String foodName, foodType, date;
    private String imageURI;

    public foodItem(String foodName, String foodType, String date, String imageURI) {
        this.foodName = foodName;
        this.foodType = foodType;
        this.date = date;
        this.imageURI = imageURI;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodType() {
        return foodType;
    }

    public String getFoodDate() {
        return date;
    }

    public String getImageURI() {
        return imageURI;
    }
}
