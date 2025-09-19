package com.example.aicodereviewapi.security;

import com.example.aicodereviewapi.entity.User;
import com.example.aicodereviewapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =
                userRepository
                        .findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new JwtUserDetails(user.getId(), user.getEmail(), user.getPassword());
    }

    public JwtUserDetails loadUserById(String id) throws UsernameNotFoundException {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        return new JwtUserDetails(user.getId(), user.getEmail(), user.getPassword());
    }
}
