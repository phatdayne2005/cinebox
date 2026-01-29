package com.phat.cinebox.controller;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String getHomePage(Model model) {
        return "/user/homepage";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model){
        return "/user/login";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model){
        return "/user/register";
    }

    @GetMapping("/checkout")
    public String getCheckoutPage(Model model){
        return "/user/checkout";
    }

    @GetMapping("/movie-booking")
    public String getMovieBookingPage(Model model){
        return "/user/movie-booking";
    }
}
