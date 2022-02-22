package com.example.jwthashmap.services;

import com.example.jwthashmap.entity.SignUpResponse;
import com.example.jwthashmap.entity.Users;

import java.util.List;

public interface CustomUserService {
    SignUpResponse saveUser(Users user);

    List<Users> getAllUsers();

    Users findOne(String username);
}
