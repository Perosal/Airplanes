package com.frozenbrain.airplanes.Model;

public class User {

    private String username;
    private String connectedUser;
    private String move;

    public User(String username, String connectedUser, String move) {
        this.username = username;
        this.connectedUser = connectedUser;
        this.move = move;
    }

    public User(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getConnectedUser() {
        return connectedUser;
    }

    public void setConnectedUser(String connectedUser) {
        this.connectedUser = connectedUser;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }
}
