package com.resolvedd.authenticatedd.repository;

import com.resolvedd.authenticatedd.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {

    Optional<Token> findByUsername(String username);
    Optional<Token> findByToken(String token);
}
