package com.phat.cinebox.controller;

import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MyErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        // Trả về lệnh redirect của Spring MVC
        return "redirect:/";
    }
}
