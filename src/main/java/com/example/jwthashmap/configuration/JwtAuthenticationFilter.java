package com.example.jwthashmap.configuration;

import com.example.jwthashmap.services.CustomUsersServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Service
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CustomUsersServiceImpl userDetailsService;
    private final JwtTokenHelper jwtTokenHelper;

    @Autowired
    public JwtAuthenticationFilter(CustomUsersServiceImpl userDetailsService, JwtTokenHelper jwtTokenHelper) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenHelper = jwtTokenHelper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authToken = jwtTokenHelper.getToken(request);
        log.info(authToken);

        if(authToken != null){
            String username = jwtTokenHelper.getUsernameFromToken(authToken);
            if(null!=username){
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if(jwtTokenHelper.validateToken(authToken,userDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
     filterChain.doFilter(request,response);
    }

}
