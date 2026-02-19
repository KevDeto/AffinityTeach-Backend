package com.affinityteach.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.affinityteach.application.dto.UsuarioAutenticadoDTO;

@Component
public class UsuarioAutenticadoProvider {
	public UsuarioAutenticadoDTO getCurrentUser(Jwt jwt) {

		return new UsuarioAutenticadoDTO(
				jwt.getSubject(),
				jwt.getClaimAsString("name"),
				jwt.getClaimAsString("email"),
				jwt.getClaimAsString("picture"));
	}
}
