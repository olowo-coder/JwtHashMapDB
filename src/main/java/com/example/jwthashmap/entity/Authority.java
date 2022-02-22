package com.example.jwthashmap.entity;

import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public class Authority implements GrantedAuthority {

    private Long id;
    private String roleCode;
    private String roleDescription;
    private Set<Users> usersSet;


    @Override
    public String getAuthority() {
        return roleCode;
    }
}
