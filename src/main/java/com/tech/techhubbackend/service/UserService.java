package com.tech.techhubbackend.service;

import com.tech.techhubbackend.DTO.DTOs.UserDetailsDTO;
import com.tech.techhubbackend.DTO.mappers.DTOMapper;
import com.tech.techhubbackend.exceptionhandling.exceptions.ImageNotFoundException;
import com.tech.techhubbackend.exceptionhandling.exceptions.UserNotFoundException;
import com.tech.techhubbackend.model.Image;
import com.tech.techhubbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DTOMapper dtoMapper;

    @Autowired
    public UserService(UserRepository userRepository, DTOMapper dtoMapper) {
        this.userRepository = userRepository;
        this.dtoMapper = dtoMapper;
    }

    public UserDetailsDTO getUserDetails(UUID userID) {
        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        return dtoMapper.userToUserDetailsDTO(userRepository.getReferenceById(userID));
    }

    public Resource getUserPicture(UUID userID) {

        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        Image image = userRepository.getReferenceById(userID).getProfileImage();
        if(image == null) throw new ImageNotFoundException();

        Path path;
        path = Paths.get("D:/TechHub/images" + image.getFilePath() + '/' + image.getFilename());
        try {
            return new ByteArrayResource(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new ImageNotFoundException();
        }
    }
}