package com.example.jwthashmap.services;

import com.example.jwthashmap.entity.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class PersonDetailsService implements UserDetailsService{
    private final Map<String, Users> db = new HashMap<>();

    @Override
    public Users loadUserByUsername(String username) throws UsernameNotFoundException {
        if(db.containsKey(username.trim())){
            throw new UsernameNotFoundException("User not found with username " + username);
        }
        log.info(String.valueOf(db.get(username)));
        return db.get(username.trim());
    }
}
