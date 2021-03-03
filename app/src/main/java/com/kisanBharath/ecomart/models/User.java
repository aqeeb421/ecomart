package com.kisanBharath.ecomart.models;


public class User {

    private String user_id;
    private String phoneNumber;
    private String userName;
    private String profilePic;
    private String email;
    private String password;
    private String status;
    private String search;


    public User(String user_id, String phoneNumber, String userName, String profilePic, String email, String password, String status, String search) {
        this.user_id = user_id;
        this.phoneNumber = phoneNumber;
        this.userName = userName;
        this.profilePic = profilePic;
        this.email = email;
        this.password = password;
        this.status = status;
        this.search = search;
    }

    public User() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }


    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", userName='" + userName + '\'' +
                ", profilePic='" + profilePic + '\'' +
                ", email='" + email + '\'' +
                ", about='" + password + '\'' +
                '}';
    }
}
