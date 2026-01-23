package com.phat.cinebox.controller;

import com.phat.cinebox.dto.request.UserCreateRequest;
import com.phat.cinebox.dto.response.UserCreateResponse;
import com.phat.cinebox.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/test")
    public ResponseEntity test(){
        return ResponseEntity.ok().body("test ok");
    }

    @PostMapping("/create-user")
    public ResponseEntity createUser(@RequestBody UserCreateRequest userCreateRequest){
        Map<String, String> response = new HashMap<>();
        try {
            UserCreateResponse userCreateResponse = userService.create(userCreateRequest);
            response.put("message", "Account created successfully!");
            return ResponseEntity.ok().body(response);
        }
        catch(Exception e){
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
