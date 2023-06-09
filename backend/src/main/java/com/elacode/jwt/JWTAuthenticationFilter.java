package com.elacode.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;


    public JWTAuthenticationFilter(JWTUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // the request will be automatically
            return;
        }
        String jwt = authHeader.substring(7); // Because from 7 onwords is the jwt token
        String subject = jwtUtil.getSubject(jwt); // extract the subject(email in our case)

        if (subject != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) { // contains info who is currently authenticated
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject); // Load the user details
            if (jwtUtil.isTokenValid(jwt, userDetails.getUsername())){ // validate the token
                UsernamePasswordAuthenticationToken authenticationToken = // create username and password authToken
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken); // set the authentication
            }

        }
        filterChain.doFilter(request, response); // move to next filter


    }



}
