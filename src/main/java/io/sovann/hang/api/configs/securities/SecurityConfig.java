package io.sovann.hang.api.configs.securities;

import io.sovann.hang.api.configs.handlers.RestAccessDeniedHandler;
import io.sovann.hang.api.configs.handlers.RestAuthenticationEntryPoint;
import io.sovann.hang.api.configs.properties.CORSProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

