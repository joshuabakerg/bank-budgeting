package za.co.joshuabakerg.bankimport.core.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.core.UserService;
import za.co.joshuabakerg.bankimport.domain.model.SignupRequest;
import za.co.joshuabakerg.bankimport.domain.entities.User;
import za.co.joshuabakerg.bankimport.domain.repositories.UserRepository;

/**
 * @author Joshua Baker on 17/12/2019
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(final SignupRequest request) {
        if (userRepository.findUserByEmail(request.getEmail()) == null) {
            final User user = User.builder()
                    .name(request.getName())
                    .surname(request.getSurname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .roles("USER")
                    .build();
            final User save = userRepository.save(user);
            System.out.println();
        } else {
            throw new RuntimeException("User already exists");
        }
    }

    @Override
    public User getByEmail(final String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public void clearAll() {
        userRepository.deleteAll();
    }
}
