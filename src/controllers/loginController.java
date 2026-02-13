package controllers;

import models.User;
import database.Queries;

public class LoginController {
    
    public User authenticate(String username, String password) {
        // Get user from database
        User user = Queries.getUserByUsername(username);
        
        if (user != null && user.getPassword().equals(password)) {
            return user; // Login successful
        }
        
        return null; // Login failed
    }
    
    public boolean validateInput(String username, String password) {
        return username != null && !username.trim().isEmpty() &&
               password != null && !password.trim().isEmpty();
    }
}