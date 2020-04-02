package za.co.joshuabakerg.bankimport.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.core.UserService;
import za.co.joshuabakerg.bankimport.domain.entities.User;
import za.co.joshuabakerg.bankimport.domain.model.SignupRequest;

/**
 * @author Joshua Baker on 06/12/2019
 */
@RestController
@AllArgsConstructor
public class AccountController {

    private UserService userService;

    @GetMapping(path = "/api/accounts/me")
    public ResponseEntity<User> signup(final Principal principal) {
        return ResponseEntity.ok(userService.getByEmail(principal.getName()));
    }

    @PostMapping(path = "/api/accounts/signup", consumes = "application/json")
    public ResponseEntity<Void> signup(final @RequestBody SignupRequest request) {
        userService.createUser(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/api/accounts")
    public ResponseEntity<Void> deleteAllAccounts() {
        userService.clearAll();
        return ResponseEntity.ok().build();
    }
}
