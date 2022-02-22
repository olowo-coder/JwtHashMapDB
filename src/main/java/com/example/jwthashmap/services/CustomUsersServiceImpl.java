package com.example.jwthashmap.services;

import com.example.jwthashmap.entity.Role;
import com.example.jwthashmap.entity.SignUpResponse;
import com.example.jwthashmap.entity.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class CustomUsersServiceImpl implements UserDetailsService, CustomUserService {
    private final Map<String, Users> db = new HashMap<>();
    private final BCryptPasswordEncoder passwordEncoder;


    @Autowired
    public CustomUsersServiceImpl(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Users loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!db.containsKey(username.trim())){
            throw new UsernameNotFoundException("User not found with username " + username);
        }
        log.info(String.valueOf(db.get(username)));
        return db.get(username.trim());
    }

    @Override
    public SignUpResponse saveUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setCreatedAt(new Date());
        log.info(String.valueOf(user));
        db.put(user.getUsername().trim(), user);
        return SignUpResponse.builder().message("Saved").build();
    }

    @Override
    public List<Users> getAllUsers() {
        List<Users> usersList = new ArrayList<>(db.values());
        return usersList;
    }

    @Override
    public Users findOne(String username) {
        return db.get(username);
    }
}
