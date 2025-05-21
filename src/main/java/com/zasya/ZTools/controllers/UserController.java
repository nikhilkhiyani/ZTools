package com.zasya.ZTools.controllers;

import com.zasya.ZTools.DTO.UserLogin;
import com.zasya.ZTools.enums.Roles;
import com.zasya.ZTools.models.User;
import com.zasya.ZTools.repositories.UserRepo;
import com.zasya.ZTools.services.UserService;
import com.zasya.ZTools.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Tag(name = "User", description = "User Authentication APIs")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "Login user", description = "Authenticates user and returns JWT token and user details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLogin userLogin, HttpServletResponse response){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLogin.getUsername(),
                            userLogin.getPassword()
                    )
            );
            User user = userRepo.findByEmail(userLogin.getUsername());
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getApps());
            Map<String, Object> apiResponse = new HashMap<>();
            apiResponse.put("token :", token);
            apiResponse.put("user", user);
            return ResponseEntity.ok(apiResponse);
        } catch (BadCredentialsException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    };

    @Operation(summary = "Register user", description = "Registers a new user and returns JWT token and user details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful registration",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "User already exists",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Auto-login failed",
                    content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user, HttpServletResponse response){
        try{

            if (userRepo.findByEmail(user.getEmail()) != null) {
                return ResponseEntity.badRequest().body("User already exists with email: " + user.getEmail());
            }

            User newUser = new User();
            newUser.setName(user.getName());
            newUser.setEmail(user.getEmail());
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            newUser.setApps(user.getApps());
            newUser.setRole(user.getRole());
            newUser.setActive(user.isActive());
            User user1 = userRepo.save(newUser);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            user.getPassword()
                    )
            );
            String token = jwtUtil.generateToken(user1.getEmail(), user1.getRole().name(), user1.getApps());

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", user1);
            return ResponseEntity.ok(result);
        } catch (BadCredentialsException exception){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Auto-login failed after registration.");
        }
    }



}
