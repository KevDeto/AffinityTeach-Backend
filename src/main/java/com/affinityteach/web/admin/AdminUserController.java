package com.affinityteach.web.admin;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuth;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @PostMapping("/{uid}/make-admin")
    public ResponseEntity<String> makeAdmin(@PathVariable String uid) throws Exception {

        FirebaseAuth.getInstance().setCustomUserClaims(
                uid,
                Map.of("roles", List.of("ADMIN"))
        );

        return ResponseEntity.ok("Usuario convertido en ADMIN");
    }
}
