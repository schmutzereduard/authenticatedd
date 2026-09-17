package com.resolvedd.authenticatedd.service;

import com.resolvedd.authenticatedd.dto.TokenDTO;
import com.resolvedd.authenticatedd.dto.UserDTO;
import com.resolvedd.authenticatedd.exception.InvalidTokenException;
import com.resolvedd.authenticatedd.mapper.TokenMapper;
import com.resolvedd.authenticatedd.model.Token;
import com.resolvedd.authenticatedd.repository.TokenRepository;
import com.resolvedd.authenticatedd.security.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.resolvedd.authenticatedd.constants.Constants.BEARER;
import static com.resolvedd.authenticatedd.constants.Constants.EMPTY;
import static com.resolvedd.authenticatedd.constants.ExceptionConstants.INVALID_TOKEN_MESSAGE;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenMapper tokenMapper;
    private final TokenRepository tokenRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    public TokenDTO getByUsername(String username) {
        return tokenMapper.toDTO(tokenRepository.findByUsername(username));
    }

    public TokenDTO getByToken(String token) {
        return tokenMapper.toDTO(tokenRepository.findByToken(token));
    }

    public TokenDTO generateToken(String username) {

        UserDTO userDTO = userService.findByUsername(username);
        String generatedToken = jwtTokenUtil.createToken(String.valueOf(userDTO.getId()));

        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setUsername(username);
        tokenDTO.setToken(String.valueOf(generatedToken));

        Token token = tokenMapper.toEntity(tokenDTO);

        return tokenMapper.toDTO(tokenRepository.save(token));
    }

    public TokenDTO isValidToken(String token) {

        token = token.startsWith(BEARER) ? token.replace(BEARER, EMPTY).trim() : token;
        Claims parsedToken = jwtTokenUtil.parseToken(token);
        long userId = Long.parseLong(parsedToken.getSubject());
        UserDTO userDTO = userService.findById(userId);
        TokenDTO tokenDTO = getByToken(token);

        if (userId != userDTO.getId() || !tokenDTO.getUsername().equals(userDTO.getUsername()))
            throw new InvalidTokenException(INVALID_TOKEN_MESSAGE);

        return tokenDTO;
    }
}
