package com.phat.cinebox.controller;

import com.phat.cinebox.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/login")
    public ResponseEntity login(@RequestParam("username") String username, @RequestParam("password") String password){
        System.out.println(username);
        System.out.println(password);
        return ResponseEntity.ok().body("success");
    }

    @PostMapping("/api/test")
    public ResponseEntity test(){
        return ResponseEntity.ok().body("test ok");
    }

    @PostMapping()
}
