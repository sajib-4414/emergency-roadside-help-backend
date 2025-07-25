package com.emergency.roadside.help.responder_assignment_backend.configs.auth;




import com.emergency.roadside.help.common_module.commonexternal.CustomUserDetails;
import com.emergency.roadside.help.common_module.commonexternal.ExternalUser;
import com.emergency.roadside.help.common_module.exceptions.ErrorDTO;
import com.emergency.roadside.help.common_module.exceptions.ErrorHttpResponse;
import com.emergency.roadside.help.common_module.exceptions.customexceptions.UnAuthorizedError;
import com.emergency.roadside.help.responder_assignment_backend.external.UserServiceClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.RetryableException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
//this class is more like a middleware
public class AuthenticationFilter extends OncePerRequestFilter {


    private final UserServiceClient authService;

    //filter chain is more like next() method
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
//        System.out.println("do internal filter invoked/....");

        try {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String username;
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("No token given, all good, passing to next filter");
                filterChain.doFilter(request, response); //not attaching any user, passing to the next filter/middleware
                return;
            }
            jwt = authHeader.substring(7);
            // 🔹 Forward token to the authentication service via Feign
            ExternalUser externalUser = authService.validateAndGetUser("Bearer " + jwt);
            // 🔹 Convert ExternalUser to Spring Security UserDetails
            UserDetails userDetails = new CustomUserDetails(externalUser);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            System.out.println("api call made to other service");
            // 🔹 Attach the user to Spring Security Context
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
            System.out.println("all good passing to next");

        }
        //any error that is thrown or happened here in the FIlter, does not get caught by the
        //global error handler, controller advice, so we are handlign here.
        catch (FeignException.Unauthorized ex) {
            ErrorDTO error = ErrorDTO.builder()
                    .code("token_wrong_v2")
                    .message("Authentication error, " + ex.getMessage())
                    .build();
            ErrorHttpResponse errorResponse = ErrorHttpResponse.builder()
                    .errors(Collections.singletonList(error))
                    .build();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
            return;
        }
        catch (InternalAuthenticationServiceException ex) {

            System.out.println("this exception happened...");
            // Handle the exception and return the error response directly
            ErrorDTO error = ErrorDTO.builder()
                    .code("token_wrong_v2")
                    .message("Authentication error, " + ex.getMessage())
                    .build();
            ErrorHttpResponse errorResponse = ErrorHttpResponse.builder()
                    .errors(Collections.singletonList(error))
                    .build();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
            return; // Prevent further processing
        }
        catch (UnAuthorizedError ex){
            ErrorDTO error = ErrorDTO.builder()
                    .code("token error")
                    .message("Authentication error, expired or wrong, " + ex.getMessage())
                    .build();
            ErrorHttpResponse errorResponse = ErrorHttpResponse.builder()
                    .errors(Collections.singletonList(error))
                    .build();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
            return;
        }
        catch (RetryableException err){
            System.out.println("feign client found other mciroservie inaccessible, error="+err);
            ErrorDTO error = ErrorDTO.builder()
                    .code("backend_unavailable")
                    .message("other backend unavailable, try later again")
                    .build();
            ErrorHttpResponse errorResponse = ErrorHttpResponse.builder()
                    .errors(Collections.singletonList(error))
                    .build();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
            return;
        }

    }
}
