package com.zasya.ZTools.services;

import com.zasya.ZTools.DTO.UserLogin;
import com.zasya.ZTools.models.User;
import com.zasya.ZTools.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public String login(UserLogin userLogin){
        User user = userRepo.findByEmail(userLogin.getUsername());
        String username = user.getEmail();
        String userpassword = user.getPassword();
        String name = userLogin.getUsername();
        String password =  userLogin.getPassword();
        String encodedPassword = passwordEncoder().encode(password);
//        Enum[] appAccess = user.getAppAccess();
        if (username.equals(name) && userpassword.equals(encodedPassword)){
            return "logged in";
//            if (appAccess.)
        }
        return ("login failed");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),            // username
                user.getPassword(),         // encoded password
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
};
