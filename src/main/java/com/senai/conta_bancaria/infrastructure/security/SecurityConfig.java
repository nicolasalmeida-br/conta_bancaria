package com.senai.conta_bancaria.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                // 🔹 IMPORTANTE PARA O H2: permitir uso de frames
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )

                .authorizeHttpRequests(auth -> auth

                        // ======================================
                        // ENDPOINTS PÚBLICOS (IMPORTANTE: EM CIMA)
                        // ======================================
                        .requestMatchers(
                                "/api/autenticacao/**",
                                "/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                // 🔹 LIBERAR O CONSOLE DO H2
                                "/h2-console/**"
                        ).permitAll()

                        // ======================================
                        // CLIENTE
                        // ======================================
                        .requestMatchers(HttpMethod.POST, "/api/cliente/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/cliente/**")
                        .hasAnyRole("ADMIN", "GERENTE", "CLIENTE")

                        // ======================================
                        // CONTA
                        // ======================================
                        .requestMatchers(HttpMethod.GET, "/api/conta/**")
                        .hasAnyRole("ADMIN", "GERENTE", "CLIENTE")

                        .requestMatchers(HttpMethod.POST, "/api/conta/**")
                        .hasRole("CLIENTE")

                        .requestMatchers(HttpMethod.PUT, "/api/conta/**")
                        .hasRole("CLIENTE")

                        .requestMatchers(HttpMethod.DELETE, "/api/conta/**")
                        .hasAnyRole("ADMIN", "GERENTE")

                        // ======================================
                        // TAXAS
                        // ======================================
                        .requestMatchers(HttpMethod.POST, "/taxas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/taxas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/taxas/**").hasRole("ADMIN")

                        // ============================
                        // PAGAMENTOS
                        // ============================
                        .requestMatchers(HttpMethod.POST, "/pagamentos/autenticacao").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/pagamentos/autenticacao/**").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/pagamentos/**").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/pagamentos/**").hasAnyRole("ADMIN", "GERENTE", "CLIENTE")

                        // ======================================
                        // TODAS AS OUTRAS REQUISIÇÕES
                        // ======================================
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(usuarioDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}