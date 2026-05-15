package com.bookshelf.idp.libraryservice.config.security;

import com.bookshelf.idp.libraryservice.client.DatabaseServiceClient;
import com.bookshelf.idp.libraryservice.model.UserModel;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final DatabaseServiceClient dbClient;

    public CustomUserDetailsService(DatabaseServiceClient dbClient) {
        this.dbClient = dbClient;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel user = dbClient.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}