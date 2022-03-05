package com.bochkov.security.jwt;

import com.bochkov.jpa.repository.UserRepository;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("admin".equals(username)) {
            return new User("admin", passwordEncoder.encode("1234"),
                    Lists.newArrayList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        } else {
            User result = userRepository.findByName(username)
                    .map(user -> new User(username, passwordEncoder.encode("1234"), Lists.newArrayList(new SimpleGrantedAuthority("ROLE_" + user.getId()))))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
            return result;
        }
    }

}
