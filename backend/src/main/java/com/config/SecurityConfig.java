package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/auth/login", "/auth/logout").permitAll()
                .antMatchers("/user/**").permitAll()
                .antMatchers("/asset/**").permitAll()
                .antMatchers("/maintenance/**").permitAll()
                .antMatchers("/category/**").permitAll()
                .antMatchers("/location/**").permitAll()
                .antMatchers("/trace/**").permitAll()
                .antMatchers("/log/**").permitAll()
                .antMatchers("/excel/**").permitAll()
                .antMatchers("/ai/**").permitAll()
                .anyRequest().authenticated();
    }
}