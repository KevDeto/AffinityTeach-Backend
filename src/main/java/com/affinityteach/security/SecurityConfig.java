package com.affinityteach.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private static final String PROJECT_ID = "affinityteach";
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.csrf(csrf -> csrf.disable())
		.cors(Customizer.withDefaults())
        .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
		.authorizeHttpRequests(auth -> auth
				
                // PUBLICOS
                .requestMatchers(HttpMethod.GET, "/api/docentes/**").permitAll()
                
                // AUTENTICADOS
                .requestMatchers(HttpMethod.POST,
                        "/api/docentes/*/resenas").authenticated()
                
                .requestMatchers(HttpMethod.POST,
                        "/api/docentes/*/resenas/*/like").authenticated()
                
                // ADMIN
                .requestMatchers("/api/docentes/admin/**").authenticated() //hasRole("ADMIN")
                .requestMatchers("/api/admin/**").authenticated() //hasRole("ADMIN")
                
				.anyRequest().authenticated())
		.oauth2ResourceServer(oauth -> oauth
				.jwt(jwt -> jwt
						.jwtAuthenticationConverter(jwtAuthenticationConverter())));
		
		return http.build();
	}

    @Bean
    JwtDecoder jwtDecoder() {
    	
        String issuer = "https://securetoken.google.com/" + PROJECT_ID;
        
        NimbusJwtDecoder jwtDecoder =
                NimbusJwtDecoder.withJwkSetUri(
                        "https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com"
                ).build();
        
        OAuth2TokenValidator<Jwt> withIssuer =
                JwtValidators.createDefaultWithIssuer(issuer);
        
        OAuth2TokenValidator<Jwt> audienceValidator = token -> {
        	
            if (token.getAudience().contains(PROJECT_ID)) {
                return OAuth2TokenValidatorResult.success();
            }
            
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Invalid audience", null)
            );
        };
        
        OAuth2TokenValidator<Jwt> validator =
                new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
        
        jwtDecoder.setJwtValidator(validator);

        return jwtDecoder;
    }

	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
		
		authoritiesConverter.setAuthorityPrefix("ROLE_");
		authoritiesConverter.setAuthoritiesClaimName("roles");
		
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		
		converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
		
		return converter;
	}
}
