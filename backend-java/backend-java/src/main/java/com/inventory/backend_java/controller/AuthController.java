package com.inventory.backend_java.controller;

import com.inventory.backend_java.model.User;
import com.inventory.backend_java.service.UserManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserManager userManager = new UserManager();

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return userManager.registerUser(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {

        boolean success = userManager.loginUser(
                user.getUsername(),
                user.getPassword()
        );

        if (success) {
            return "Login successful";
        } else {
            return "Invalid credentials";
        }
    }
}