package com.zasya.ZTools.controllers;

import com.zasya.ZTools.enums.App;
import com.zasya.ZTools.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Controller
public class HomeController {

    @Autowired
    private JwtUtil jwtUtil;

//    @GetMapping("/login")
//    public String homePage(){
//        return "login";
//    }

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        String token = extractTokenFromHeader(request); // Bearer <token>
        if (token != null && jwtUtil.validateToken(token)) {
            List<String> apps = jwtUtil.getAppsFromToken(token); // Extract from claim
            model.addAttribute("apps", apps);
        } else {
            model.addAttribute("apps", Collections.emptyList());
        }

        return "dashboard";
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }



}
