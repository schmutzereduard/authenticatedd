package com.resolvedd.authenticatedd.service;

import com.resolvedd.authenticatedd.dto.Credentials;
import com.resolvedd.authenticatedd.dto.UserDTO;
import com.resolvedd.authenticatedd.mapper.UserMapper;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserDTO findByUsername(String username) {
        return userMapper.toDTO(userRepository.findByUsername(username));
    }

    public UserDTO saveUser(String username, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));

        return userMapper.toDTO(userRepository.save(user));
    }

    public boolean isUserValid(Credentials credentials) {

        User user = userRepository.findByUsername(credentials.getUsername());
        if (user == null)
            return false;

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return encoder.matches(credentials.getPassword(), user.getPassword());
    }
}
