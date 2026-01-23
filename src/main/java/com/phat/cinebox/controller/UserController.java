package com.phat.cinebox.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @PostMapping("/api/login")
    public ResponseEntity login(@RequestParam("username") String username, @RequestParam("password") String password){
        System.out.println(username);
        System.out.println(password);
        return ResponseEntity.ok().body("success");
    }
}
