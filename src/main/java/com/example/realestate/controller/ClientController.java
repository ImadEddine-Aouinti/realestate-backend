package com.example.realestate.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
@PreAuthorize("hasRole('USER')")
public class ClientController {

    @GetMapping("/dashboard")
    public String clientDashboard() {
        return "{\"message\": \"Welcome to Client Dashboard\", \"role\": \"CLIENT\"}";
    }

    @GetMapping("/profile")
    public String clientProfile() {
        return "{\"message\": \"Client profile page\"}";
    }
}