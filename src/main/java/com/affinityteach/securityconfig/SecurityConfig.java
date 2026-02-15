package com.affinityteach.securityconfig;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http))
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            	
                .requestMatchers(HttpMethod.GET, "/api/docentes").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/docentes/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/docentes/{id}/resenas").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/docentes/{id}/resenas").hasRole("USER")
                
                // PARA QUE NADIE PUEDA MODIFICAR NADA (luego implementar roles)
                .requestMatchers(HttpMethod.POST, "/api/docentes").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/docentes/importar-docentes").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/docentes/cargar-iniciales").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/docentes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/docentes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/docentes/{id}/resenas/*/like").hasRole("ADMIN")
                
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/ping").permitAll()
                
                // TODO LO DEMAS CON AUTENTICACION
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        
        return http.build();
    }
    
    @Bean
    public JwtDecoder jwtDecoder() {
        // Validar tokens de Google (firebase tiene uno propio)
        return NimbusJwtDecoder
                .withJwkSetUri("https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com")
                .build();    }
    
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            return authorities;
        });
        return converter;
    }
}
