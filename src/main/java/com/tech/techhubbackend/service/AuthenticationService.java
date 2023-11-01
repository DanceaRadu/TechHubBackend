package com.tech.techhubbackend.service;

import com.tech.techhubbackend.DTO.DTOs.AuthenticationRequest;
import com.tech.techhubbackend.DTO.DTOs.AuthenticationResponse;
import com.tech.techhubbackend.DTO.DTOs.GoogleRegisterRequest;
import com.tech.techhubbackend.DTO.DTOs.RegisterRequest;
import com.tech.techhubbackend.auth.Role;
import com.tech.techhubbackend.exceptionhandling.exceptions.*;
import com.tech.techhubbackend.model.EmailVerification;
import com.tech.techhubbackend.model.Image;
import com.tech.techhubbackend.model.User;
import com.tech.techhubbackend.repository.EmailVerificationRepository;
import com.tech.techhubbackend.repository.ImageRepository;
import com.tech.techhubbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.FileOutputStream;
import java.io.IOException;
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
    private final WebClient.Builder webClientBuilder;
    private final ImageRepository imageRepository;

    @Autowired
    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager,
                                 EmailVerificationRepository emailVerificationRepository,
                                 EmailService emailService,
                                 WebClient.Builder webClientBuilder,
                                 ImageRepository imageRepository
                                 ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.emailVerificationRepository = emailVerificationRepository;
        this.emailService = emailService;
        this.webClientBuilder = webClientBuilder;
        this.imageRepository = imageRepository;
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

    public AuthenticationResponse googleRegister(GoogleRegisterRequest request) {
        User user = new User();
        user.setRole(Role.USER);
        user.setVerified(true);
        user.setEmail(request.getEmail());
        user.setFirstName(request.getGiven_name());
        user.setLastName(request.getFamily_name());
        user.setGoogleId(request.getId());

        int indexOfStopChar = request.getEmail().indexOf('@');
        if(userRepository.existsBy_username(request.getEmail().substring(0, indexOfStopChar)))
            user.set_username(request.getEmail().substring(0, indexOfStopChar) + UUID.randomUUID());
        else
            user.set_username(request.getEmail().substring(0, indexOfStopChar));

        if(userRepository.existsByGoogleId(request.getId())) throw new EntityAlreadyExistsException("This google account is already associated to a user");
        if(userRepository.existsByEmail(user.getEmail())) throw new EntityAlreadyExistsException("User with this email already exists");
        userRepository.save(user);

        //save google profile image
        UUID imageUUID = UUID.randomUUID();
        String uploadDirectory = "D:/TechHub/images/user/" + user.getUserID();
        try {
            Resource resource = new FileSystemResource(uploadDirectory);
            if (!resource.exists() || !resource.getFile().isDirectory()) {
                if (!resource.getFile().mkdirs())
                    throw new InternalServerErrorException("Could not create user folder");
            }
            FileUtils.cleanDirectory(resource.getFile());

            WebClient webClient = webClientBuilder.baseUrl(request.getPicture()).build();

            Mono<byte[]> imageMono = webClient.get()
                    .uri("")
                    .retrieve()
                    .bodyToMono(byte[].class);

            byte[] imageBytes = imageMono.block();
            if (imageBytes != null) {
                FileOutputStream out = new FileOutputStream("D:/TechHub/images/user/" + user.getUserID() + "/" + imageUUID + ".png");
                out.write(imageBytes);
            }

            //create a new image entity based on the file that was just saved to disk
            Image imageEntity = new Image();
            imageEntity.setFilename(imageUUID + ".png");
            imageEntity.setFilePath(uploadDirectory);
            imageRepository.save(imageEntity);

            user.setProfileImage(imageEntity);
            userRepository.save(user);

        } catch (IOException e) {
            throw new InternalServerErrorException("Could not create user folder");
        }

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse googleSignUp(GoogleRegisterRequest request) {
        if(!userRepository.existsByGoogleId(request.getId())) throw new EntityNotFoundException();
        User user = userRepository.getUserByGoogleId(request.getId()).orElseThrow(UserNotFoundException::new);

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