package com.inventory.backend_java.service;

import com.inventory.backend_java.model.User;

public class UserManager {

    public String registerUser(User user) {
        System.out.println("User registered: " + user.getUsername());
        return "User registered: " + user.getUsername();
    }

    public boolean loginUser(String username, String password) {

        return username.equals("admin")
                && password.equals("admin123");
    }
}