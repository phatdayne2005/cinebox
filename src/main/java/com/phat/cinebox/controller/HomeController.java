package com.phat.cinebox.controller;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String getHomePage(Model model) {
        return "homepage";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model){
        return "login";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model){
        return "register";
    }

    @GetMapping("/checkout")
    public String getCheckoutPage(Model model){
        return "checkout";
    }

    @GetMapping("movie-booking")
    public String getMovieBookingPage(Model model){
        return "movie-booking";
    }
}
