package com.eyecommer.Backend.service;

import com.eyecommer.Backend.exception.ResourceNotFoundException;
import com.eyecommer.Backend.model.Token;
import com.eyecommer.Backend.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    public int save(Token token) {
        Optional<Token> optional = tokenRepository.findByUsername(token.getUsername());
        if (optional.isEmpty()) {
            tokenRepository.save(token);
            return token.getId();
        } else {
            Token t = optional.get();
            t.setAccessToken(token.getAccessToken());
            t.setRefreshToken(token.getRefreshToken());
            tokenRepository.save(t);
            return t.getId();
        }
    }
    public void delete(String username) {
        Token token = getByUsername(username);
        tokenRepository.delete(token);
    }
    public Token getByUsername(String username) {
        return tokenRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Not found token"));
    }
    public void clearResetToken(String username) {
        Token token = getByUsername(username);
        token.setResetToken(null);
        tokenRepository.save(token);
    }
}
