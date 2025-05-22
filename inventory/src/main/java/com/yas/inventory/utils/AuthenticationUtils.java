package com.yas.inventory.utils;

import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.inventory.constants.ApiConstant;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class AuthenticationUtils {

    private AuthenticationUtils() {
    }

    public static String extractUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException(ApiConstant.ACCESS_DENIED);
        }

        JwtAuthenticationToken contextHolder = (JwtAuthenticationToken) authentication;

        return contextHolder.getToken().getSubject();
    }

    public static String extractJwt() {
        return ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
    }
}