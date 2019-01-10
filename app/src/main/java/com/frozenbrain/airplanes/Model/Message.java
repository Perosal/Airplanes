package com.frozenbrain.airplanes.Model;

public class Message {
    public String message;

    public Message(String message) {
        this.message = message;
    }

    public Message(){};

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
