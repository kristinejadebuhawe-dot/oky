package com.ordering.jan.service;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ordering.jan.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        com.ordering.jan.entity.User user =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found"));

        String role = "USER";
        if (user.getRole() != null && user.getRole().startsWith("ROLE_")) {
            role = user.getRole().replace("ROLE_", "");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole())
                .build();
    }
}