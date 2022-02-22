package com.example.jwthashmap.configuration;

import com.example.jwthashmap.services.CustomUsersServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final CustomUsersServiceImpl usersServices;
    private final JwtTokenHelper jwtTokenHelper;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfiguration(CustomUsersServiceImpl usersServices, JwtTokenHelper jwtTokenHelper, AuthenticationEntryPoint authenticationEntryPoint, BCryptPasswordEncoder passwordEncoder) {
        this.usersServices = usersServices;
        this.jwtTokenHelper = jwtTokenHelper;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(usersServices).passwordEncoder(passwordEncoder);
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                        .and().authorizeRequests((request) -> request.antMatchers("/roll/**","/auth/login").permitAll()
                        .antMatchers(HttpMethod.OPTIONS,"/**").permitAll().anyRequest().authenticated())
                        .addFilterBefore(new JwtAuthenticationFilter(usersServices, jwtTokenHelper)
                        , UsernamePasswordAuthenticationFilter.class);

        http.cors().disable();
        http.csrf().disable().headers().frameOptions().disable();
        http.httpBasic();

    }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
}



