package com.resolvedd.authenticatedd.mapper;

import com.resolvedd.authenticatedd.dto.UserDTO;
import com.resolvedd.authenticatedd.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);
    User toEntity(UserDTO userDTO);
}
