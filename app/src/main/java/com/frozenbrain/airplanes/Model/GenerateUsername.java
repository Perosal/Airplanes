package com.frozenbrain.airplanes.Model;

import android.os.Build;

import java.util.Random;

public class GenerateUsername {
    private String username;

    public GenerateUsername() {

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 4;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
       this.username = (Build.MODEL + " - " + buffer.toString()).toUpperCase();
    }

    public String getUsername(){
        return this.username;
    }
}
