package com.affinityteach.web.admin;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
	
    @PostMapping("/{uid}/make-admin")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void makeAdmin(@PathVariable String uid) throws FirebaseAuthException {
    	
        FirebaseAuth.getInstance().setCustomUserClaims(
                uid,
                Map.of("roles", List.of("ADMIN"))
        );
    }
}
