package com.keakimleang.digital_menu.configs.securities;

import com.keakimleang.digital_menu.configs.handlers.*;
import com.keakimleang.digital_menu.configs.properties.*;
import java.util.*;
import lombok.*;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.*;
import org.springframework.security.core.context.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.servlet.util.matcher.*;
import org.springframework.web.cors.*;
import org.springframework.web.servlet.handler.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;

    private final CORSProperties corsProperties;

    @Bean
    public TokenAuthFilter tokenAuthenticationFilter() {
        return new TokenAuthFilter();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .anonymous(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                        .accessDeniedHandler(new RestAccessDeniedHandler())
                )
                .authorizeHttpRequests(authorize ->
                        authorize.requestMatchers(UnRestrictedEndpoint.FREE_URLS)
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl(UnRestrictedEndpoint.LOGOUT_URL)
                                .addLogoutHandler((request, response, authentication) -> {
                                    final String header = request.getHeader("Authorization");
                                    final String jwt;
                                    if (header == null || !header.startsWith("Bearer ")) {
                                        return;
                                    }
                                    jwt = header.substring(7);
                                    if (!jwt.isEmpty()) {
                                        SecurityContextHolder.clearContext();
                                    }
                                })
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                )
                .getOrBuild();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedHeaders(List.of("Authorization"));
        for (String corsProperty : corsProperties.getAllowedOrigins()) {
            configuration.addAllowedOrigin(corsProperty);
        }
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Requestor-Type", "Content-Type", "Access-Control-Allow-Headers", "Access-Control-Allow-Origin"));
        configuration.setExposedHeaders(Arrays.asList("X-Get-Header", "Access-Control-Allow-Methods", "Access-Control-Allow-Origin"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

