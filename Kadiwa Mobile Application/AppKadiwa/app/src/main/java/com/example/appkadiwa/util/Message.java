package com.example.appkadiwa.util;

public class Message {
    private String messageText;
    private long timestamp;

    public Message() {
        // Empty constructor required for Firestore
    }

    public Message(String messageText, long timestamp) {
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}