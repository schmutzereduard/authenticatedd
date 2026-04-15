package com.resolvedd.authenticatedd.service;

import com.resolvedd.authenticatedd.dto.TokenDTO;
import com.resolvedd.authenticatedd.mapper.TokenMapper;
import com.resolvedd.authenticatedd.model.Token;
import com.resolvedd.authenticatedd.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenMapper tokenMapper;
    private final TokenRepository tokenRepository;

    public TokenDTO getByUsername(String username) {
        return tokenMapper.toDTO(tokenRepository.findByUsername(username));
    }

    public TokenDTO getByToken(String token) {
        return tokenMapper.toDTO(tokenRepository.findByToken(token));
    }

    public TokenDTO generateToken(String username) {

        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setUsername(username);
        tokenDTO.setToken(String.valueOf(UUID.randomUUID()));
        tokenDTO.setExpiresAt(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2));

        Token token = tokenMapper.toEntity(tokenDTO);

        return tokenMapper.toDTO(tokenRepository.save(token));
    }
}
