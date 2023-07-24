package com.tech.techhubbackend.service;

import com.tech.techhubbackend.DTO.DTOs.AuthenticationRequest;
import com.tech.techhubbackend.DTO.DTOs.AuthenticationResponse;
import com.tech.techhubbackend.DTO.DTOs.RegisterRequest;
import com.tech.techhubbackend.auth.Role;
import com.tech.techhubbackend.exceptionhandling.exceptions.EmailVerificationNotFoundException;
import com.tech.techhubbackend.exceptionhandling.exceptions.EntityAlreadyExistsException;
import com.tech.techhubbackend.exceptionhandling.exceptions.UserNotFoundException;
import com.tech.techhubbackend.model.EmailVerification;
import com.tech.techhubbackend.model.User;
import com.tech.techhubbackend.repository.EmailVerificationRepository;
import com.tech.techhubbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;

    @Autowired
    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager,
                                 EmailVerificationRepository emailVerificationRepository,
                                 EmailService emailService
                                 ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.emailVerificationRepository = emailVerificationRepository;
        this.emailService = emailService;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        User user = new User();
        user.setRole(Role.USER);
        user.setVerified(false);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.set_username(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        if(userRepository.existsBy_username(user.get_username())) throw new EntityAlreadyExistsException("User with this username already exists");
        if(userRepository.existsByEmail(user.getEmail())) throw new EntityAlreadyExistsException("User with this email already exists");
        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        var user = userRepository.getUserBy_username(request.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserID().toString(), request.getPassword()));

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }

    public void createEmailToken(UUID userID) {
        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        Optional<EmailVerification> optional = emailVerificationRepository.getEmailVerificationByUser(userRepository.getReferenceById(userID));
        optional.ifPresent(emailVerificationRepository::delete);

        EmailVerification verification = new EmailVerification();
        verification.setUser(userRepository.getReferenceById(userID));
        emailVerificationRepository.save(verification);

        //send email to user
        String email = userRepository.getReferenceById(userID).getEmail();
        emailService.sendEmail(email, "Tech hub email verification", "http://localhost:3000/verifymail/" + verification.getUser().getUserID());
    }

    public boolean verifyEmail(UUID userID) {

        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        Optional<EmailVerification> optional = emailVerificationRepository.getEmailVerificationByUser(userRepository.getReferenceById(userID));
        if(optional.isEmpty()) throw new EmailVerificationNotFoundException();

        EmailVerification verification = optional.get();
        User user = verification.getUser();
        user.setVerified(true);
        userRepository.save(user);

        emailVerificationRepository.delete(verification);
        return true;
    }
}