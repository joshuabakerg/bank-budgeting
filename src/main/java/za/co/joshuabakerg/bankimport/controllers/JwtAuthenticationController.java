package za.co.joshuabakerg.bankimport.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.controllers.model.JwtRequest;
import za.co.joshuabakerg.bankimport.controllers.model.JwtResponse;
import za.co.joshuabakerg.bankimport.core.UserService;
import za.co.joshuabakerg.bankimport.core.impl.UserDetailsServiceImpl;
import za.co.joshuabakerg.bankimport.domain.entities.User;
import za.co.joshuabakerg.bankimport.utils.JwtTokenUtil;

/**
 * @author Joshua Baker on 18/12/2019
 */
@Controller
@CrossOrigin
@AllArgsConstructor
public class JwtAuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    private final UserDetailsServiceImpl userDetailsService;
    private final UserService userService;

    @RequestMapping(value = "/api/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final User user = userService.getByEmail(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

}
