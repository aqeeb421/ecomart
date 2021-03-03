package com.kisanBharath.ecomart.models;


public class Message {

    public java.util.Date Date;
    public String Text;
    public String Sender;

    public Message() {
    }

    public Message(java.util.Date date, String text, String sender) {
        Date = date;
        Text = text;
        Sender = sender;
    }

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.util.Date date) {
        Date = date;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }
}
