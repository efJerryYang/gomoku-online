package me.efjerryyang.gomokuonline.controller;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.efjerryyang.gomokuonline.dto.TokenDTO;
import me.efjerryyang.gomokuonline.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    @Autowired
    private JwtService jwtService;

    @PostMapping("/token")
    public TokenResponse getToken(@RequestBody TokenDTO tokenDTO) {
        if (tokenDTO == null || tokenDTO.getTmpId() == null) {
            // TODO: Modify the differences between the UserController
            return new TokenResponse();
        }
        String token = jwtService.generateToken(tokenDTO.getTmpId());
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
