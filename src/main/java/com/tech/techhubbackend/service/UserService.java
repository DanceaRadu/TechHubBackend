package com.tech.techhubbackend.service;

import com.tech.techhubbackend.DTO.DTOs.UserDetailsDTO;
import com.tech.techhubbackend.DTO.mappers.DTOMapper;
import com.tech.techhubbackend.exceptionhandling.exceptions.UserNotFoundException;
import com.tech.techhubbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}