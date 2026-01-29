package com.phat.cinebox.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminController {
    @GetMapping("/admin")
    public String getAdminPage(){
        return "/admin/dashboard";
    }

    @GetMapping("/admin/movies")
    public String getMoviesPage(){
        return "/admin/movies";
    }
}
