package me.efjerryyang.gomokuonline.controller;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.efjerryyang.gomokuonline.dto.TokenDTO;
import me.efjerryyang.gomokuonline.service.JwtService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    private static final Logger logger = LoggerFactory.getLogger(LoggerFactory.class);

    @Autowired
    private JwtService jwtService;

    @PostMapping("/token")
    public TokenResponse getToken(@RequestBody TokenDTO tokenDTO) {
        logger.info("POST /api/token (tokenDTO: " + tokenDTO + ")");
        if (tokenDTO == null || tokenDTO.getClientId() == null) {
            return new TokenResponse();
        }
        String token = jwtService.generateToken(tokenDTO.getClientId());
        logger.info("Token: " + token);
        return new TokenResponse(token);
    }

    @Data
    @NoArgsConstructor
    private static class TokenResponse {
        private String token;

        public TokenResponse(String token) {
            this.token = token;
        }

    }
}
