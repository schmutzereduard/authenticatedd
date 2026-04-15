package com.resolvedd.authenticatedd.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "token")
@Data
@NoArgsConstructor
public class Token {

    @Id
    private String username;
    private String token;
    private long expiresAt;
}
