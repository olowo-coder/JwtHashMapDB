package com.example.jwthashmap.controller;

import com.example.jwthashmap.entity.SignUpResponse;
import com.example.jwthashmap.entity.Users;
import com.example.jwthashmap.services.CustomUserService;
import com.example.jwthashmap.services.CustomUsersServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roll")
@Slf4j
public class AppController {

    private final CustomUsersServiceImpl usersServices;

    @Autowired
    public AppController(CustomUsersServiceImpl usersServices) {
        this.usersServices = usersServices;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(){
        return ResponseEntity.ok(usersServices.getAllUsers());
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SignUpResponse> addUsers(@RequestBody Users user){
        log.info(String.valueOf(user));
        return ResponseEntity.ok(usersServices.saveUser(user));
    }

    @GetMapping("/one/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Users> fetchOne(@PathVariable String username){
        log.info(String.valueOf(username));
        return ResponseEntity.ok(usersServices.loadUserByUsername(username.trim()));
    }
}
