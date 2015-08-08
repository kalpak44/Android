package com.example.kalpak44.mychat.models;

/**
 * Created by kalpak44 on 15-8-5.
 */
public class Message {

    private String message;
    private boolean left;

    public Message(boolean left, String message) {
        super();
        this.left = left;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    public boolean getLeft() {
        return left;
    }
}
