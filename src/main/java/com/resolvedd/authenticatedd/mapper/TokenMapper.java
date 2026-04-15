package com.resolvedd.authenticatedd.mapper;

import com.resolvedd.authenticatedd.dto.TokenDTO;
import com.resolvedd.authenticatedd.model.Token;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TokenMapper {

    TokenDTO toDTO(Token token);
    Token toEntity(TokenDTO tokenDTO);
}
