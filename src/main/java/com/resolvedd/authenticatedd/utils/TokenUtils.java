package com.resolvedd.authenticatedd.utils;

import com.resolvedd.authenticatedd.dto.TokenDTO;

import java.util.UUID;

import static com.resolvedd.authenticatedd.constants.Constants.BEARER;
import static com.resolvedd.authenticatedd.constants.Constants.EMPTY;

public class TokenUtils {

    public static boolean isTokenValid(long expiresAt) {

        return System.currentTimeMillis() < expiresAt;
    }

    public static String getToken(String bearer) {

        return bearer.replace(BEARER, EMPTY).trim();
    }
}
