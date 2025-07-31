package com.emergency.roadside.help.client_booking_backend.configs;


import com.emergency.roadside.help.client_booking_backend.configs.auth.JWTAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class GlobalSecurityConfiguration {

    //using final and required args construction means spring will automatically inject
//    private final JWTAuthFilter jwtAuthFilter;
//    private final AuthenticationProvider authenticationProvider;
    private final JWTAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .headers(header-> header.frameOptions(options-> options.sameOrigin()))
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())

                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/api/v1/auth/validate-and-get-user").authenticated()
                                .requestMatchers("/api/v1/auth/**").permitAll()
                                .requestMatchers("/api/v1/cqrs/**").permitAll()
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/error").permitAll()
                                //for any other URL, just regylar autghenticatioin is imposed
                                .anyRequest()
                                .authenticated()

                               // .requestMatchers("/**").permitAll() //means alloweing all request, do it only for testing
                )
                .sessionManagement( sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource()))
                .build();

    }
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.addAllowedOriginPattern("*"); // Use this instead of setAllowedOrigins for wildcard support, to allow all for development
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000","http://localhost:3010","http://localhost:3011"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
