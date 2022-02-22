package com.example.jwthashmap.controller;

import com.example.jwthashmap.configuration.JwtTokenHelper;
import com.example.jwthashmap.entity.AuthenticationRequest;
import com.example.jwthashmap.entity.LoginResponse;
import com.example.jwthashmap.entity.UserInfo;
import com.example.jwthashmap.entity.Users;
import com.example.jwthashmap.services.CustomUsersServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.spec.InvalidKeySpecException;

@RestController
@Slf4j
public class AuthenticationController {
    private AuthenticationManager authenticationManager;
    private JwtTokenHelper jwtTokenHelper;
    private CustomUsersServiceImpl userDetailsService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JwtTokenHelper jwtTokenHelper, CustomUsersServiceImpl userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenHelper = jwtTokenHelper;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        log.info(String.valueOf(authenticationRequest));
//       authenticate(authenticationRequest.getUserName(), authenticationRequest.getPassword());
        Users userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUserName());
        log.info("Finish Checking if exist");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, authenticationRequest.getPassword(), userDetails.getAuthorities());

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            log.debug(String.format("Auto login %s successfully!", authenticationRequest.getUserName()));
        }
       log.info("passed here");

//        SecurityContextHolder.getContext().setAuthentication(authentication);

//        Users user = (Users) authentication.getPrincipal();
//        final UserDetails userDetails = userDetailsService
//                .loadUserByUsername(authenticationRequest.getUserName());
        String jwtToken = jwtTokenHelper.generateToken(userDetails.getUsername());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/auth/userinfo")
    public ResponseEntity<?> getUserInfo() {
        return ResponseEntity.ok("Access Granted");
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }


    public void autoLogin(String username, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            log.debug(String.format("Auto login %s successfully!", username));
        }
    }
}
