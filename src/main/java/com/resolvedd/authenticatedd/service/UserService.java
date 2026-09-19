package com.resolvedd.authenticatedd.service;

import com.resolvedd.authenticatedd.dto.Credentials;
import com.resolvedd.authenticatedd.dto.UserDTO;
import com.resolvedd.authenticatedd.exception.InvalidCredentialsException;
import com.resolvedd.authenticatedd.exception.UserAlreadyExistsException;
import com.resolvedd.authenticatedd.exception.UserNotFoundException;
import com.resolvedd.authenticatedd.mapper.UserMapper;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import static com.resolvedd.authenticatedd.constants.Constants.*;
import static com.resolvedd.authenticatedd.constants.ExceptionConstants.*;
import static com.resolvedd.authenticatedd.utils.StringUtils.isNullOrEmpty;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserDTO findById(Long id) {
        return userMapper.toDTO(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE)));
    }

    public UserDTO findByUsername(String username) {
        return userMapper.toDTO(userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE)));
    }

    public UserDTO registerUser(String authHeader) {

        String[] credentials = decodeBasicAuthHeader(authHeader);

        Optional<User> optUser = userRepository.findByUsername(credentials[0]);
        if (optUser.isPresent())
            throw new UserAlreadyExistsException(USER_ALREADY_EXISTS_MESSAGE);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        User user = new User();
        user.setUsername(credentials[0]);
        user.setPassword(encoder.encode(credentials[1]));

        return userMapper.toDTO(userRepository.save(user));
    }

    public String isUserValid(String authHeader) {

        String[] credentials = decodeBasicAuthHeader(authHeader);

        Optional<User> optUser = userRepository.findByUsername(credentials[0]);
        if (optUser.isPresent()) {
            User user = optUser.get();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            return encoder.matches(credentials[1], user.getPassword()) ? user.getUsername() : null;
        } else {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE);
        }
    }

    private String[] decodeBasicAuthHeader(String authHeader) {

        if (isNullOrEmpty(authHeader)) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE);
        }

        authHeader = authHeader.startsWith(BASIC) ? authHeader.replaceFirst(BASIC, EMPTY).trim() : authHeader;

        byte[] decodedBytes = Base64.getDecoder().decode(authHeader);
        String credentials = new String(decodedBytes, StandardCharsets.UTF_8);

        String[] values = credentials.split(COLON, 2);

        if (values.length != 2) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE);
        }

        return values;
    }
}
