package com.bkb.scanner.config;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Password {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password123";
        String encodedPassword = encoder.encode(rawPassword);
        //password123
        //$2a$10$z41TNs94HV0grip7RMCcie36dklmHALKnlcpZl9xTG5lxg68PyzBC
        System.out.println("Encoded password: " + encodedPassword);
        // Use this output in your SQL
    }
}