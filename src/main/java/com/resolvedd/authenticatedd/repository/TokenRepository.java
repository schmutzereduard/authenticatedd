package com.resolvedd.authenticatedd.repository;

import com.resolvedd.authenticatedd.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {

    Token findByUsername(String username);
    Token findByToken(String token);
}
