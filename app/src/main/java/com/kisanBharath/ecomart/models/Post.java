package com.kisanBharath.ecomart.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class Post {

    @SerializedName("contact_email")
    @Expose
    private String contactEmail;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("images")
    @Expose
    private HashMap<String, String> images;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("main_categories")
    @Expose
    private String mainCategories;
    @SerializedName("post_id")
    @Expose
    private String postId;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("user_id")
    @Expose
    private String userId;
    private String phoneNumber;
    /*private Map<String, String> timestamp;

    public void setTimestamp(Map<String, String> timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }*/

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<String, String> getImages() {
        return images;
    }

    public void setImages(HashMap<String, String> images) {
        this.images = images;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMainCategories() {
        return mainCategories;
    }

    public void setMainCategories(String mainCategories) {
        this.mainCategories = mainCategories;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
