package com.banco.core.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FaviconController {

    @GetMapping("/favicon.ico")
    public String favicon() {
        return "redirect:/assets/RB_logo_sin_fondo.png";
    }
}
