package com.tech.techhubbackend.DTO.mappers;

import com.tech.techhubbackend.DTO.DTOs.ShoppingCartEntryDTO;
import com.tech.techhubbackend.DTO.DTOs.UserDetailsDTO;
import com.tech.techhubbackend.model.ShoppingCartEntry;
import com.tech.techhubbackend.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DTOMapper {
    UserDetailsDTO userToUserDetailsDTO(User user);
    ShoppingCartEntryDTO shoppingCartEntryToShoppingCartEntryDTO(ShoppingCartEntry shoppingCartEntry);

}
