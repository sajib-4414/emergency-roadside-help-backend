package com.emergency.roadside.help.responder_assignment_backend.configs.auth;


import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@AllArgsConstructor
public class AuthConfig {

   // private final UserRepository repository;
//    @Bean
//    public UserDetailsService userDetailsService() {
////        return username -> repository.findByEmail(username)
////                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        return username -> {
//            User user = repository.findByUsername(username)
//                    .orElseThrow(() -> new ItemNotFoundException("User not found"));
//            return user;
//
//            // Assuming your User entity has a method getRoles() that returns a collection of roles
////            Collection<GrantedAuthority> authorities = user
////                    .getRoles()
////                    .stream()
////                    .map(role -> new SimpleGrantedAuthority(role.getName()))
////                    .collect(Collectors.toList());
////            return new org.springframework.security.core.userdetails.User(
////                    user.getUsername(),
////                    user.getPassword(),
////                    authorities
////            );
//        };
//    }

//    @Bean
//    public AuthenticationProvider authenticationProvider(){
//        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//        authenticationProvider.setUserDetailsService(userDetailsService());
//        authenticationProvider.setPasswordEncoder(passwordEncoder());
//        return authenticationProvider;
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}