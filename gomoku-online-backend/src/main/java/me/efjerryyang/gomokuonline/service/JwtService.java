package me.efjerryyang.gomokuonline.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import me.efjerryyang.gomokuonline.Constant;
import me.efjerryyang.gomokuonline.util.CryptoUtil;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

//    public static final String SECRET_KEY = CryptoUtil.generateKey();
    public static final String SECRET_KEY = "mySecretKey";

    public String generateToken(String tmpId) {
        long expirationTime = Constant.DAY_TO_SECONDS * 1000;

        return Jwts.builder()
                .setId(tmpId)
                .setSubject(tmpId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes())
                .claim("role", "user")
                .compact();
    }

    public String getTmpIdFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody()
                .getId();
    }
}
