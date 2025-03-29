package com.keakimleang.digital_menu.configs.securities;

import com.keakimleang.digital_menu.features.users.entities.*;
import com.keakimleang.digital_menu.features.users.securities.*;
import com.keakimleang.digital_menu.features.users.services.*;
import com.keakimleang.digital_menu.utils.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.lang.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.*;
import org.springframework.security.web.authentication.*;
import org.springframework.util.*;
import org.springframework.web.filter.*;

public class TokenAuthFilter extends OncePerRequestFilter {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserServiceImpl userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String username;
        try {
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt) && tokenProvider.isTokenNotExpired(jwt) && tokenProvider.validateToken(jwt)) {
                username = tokenProvider.getUsernameFromToken(jwt);
                User user = userService.findByUsername(username);
                CustomUserDetails customUserDetails = new CustomUserDetails(user);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        customUserDetails,
                        null,
                        customUserDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
