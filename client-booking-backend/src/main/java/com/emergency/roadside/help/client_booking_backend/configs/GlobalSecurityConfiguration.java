package com.emergency.roadside.help.client_booking_backend.configs;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class GlobalSecurityConfiguration {

    //using final and required args construction means spring will automatically inject
//    private final JWTAuthFilter jwtAuthFilter;
//    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .headers(header-> header.frameOptions(options-> options.sameOrigin()))
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())

                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/**").permitAll()
                )

                .build();

    }

}
