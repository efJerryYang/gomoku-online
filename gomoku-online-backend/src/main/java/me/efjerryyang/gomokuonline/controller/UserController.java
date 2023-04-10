package me.efjerryyang.gomokuonline.controller;

import io.jsonwebtoken.JwtException;
import me.efjerryyang.gomokuonline.dto.MatchGetDTO;
import me.efjerryyang.gomokuonline.dto.PickDTO;
import me.efjerryyang.gomokuonline.entity.User;
import me.efjerryyang.gomokuonline.service.JwtService;
import me.efjerryyang.gomokuonline.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/pick")
    public ResponseEntity<String> pickUsername(@RequestHeader("Authorization") String token, @RequestBody PickDTO pickDTO) {
        try {
            String userId = jwtService.getTmpIdFromToken(token);
            if (pickDTO == null || pickDTO.getUsername() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please enter a username");

            }
            String username = pickDTO.getUsername().trim();
            if (username.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username cannot be all spaces");
            }
            User newUser = userService.pickUsername(username, userId);
            if (newUser != null) {
                userService.updateWaitingList(newUser);
                return ResponseEntity.ok("Successfully joined the matching list");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to join the matching list");
            }

        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

    }

    // TODO: Offline user detection
    @GetMapping("/match")
    public ResponseEntity<List<MatchGetDTO>> getWaitingList() {
        List<MatchGetDTO> matchGetDTOList = userService.getWaitingList();
        return ResponseEntity.ok(matchGetDTOList);
    }
//
//    @PostMapping("/match")
//    public ResponseEntity<GameDTO> matchWithPlayer(@RequestBody MatchGetDTO matchDTO) {
//        GameDTO gameDTO = userService.matchWithPlayer(matchDTO.getUsername(), matchDTO.getOpponentId());
//        if (gameDTO == null) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        } else {
//            return ResponseEntity.ok(gameDTO);
//        }
//    }
//
//    @PostMapping("/match/dice")
//    public ResponseEntity<GameDTO> matchWithRandomPlayer(@RequestBody PickDTO pickDTO) {
//        GameDTO gameDTO = userService.matchWithRandomPlayer(pickDTO.getUsername());
//        if (gameDTO == null) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        } else {
//            return ResponseEntity.ok(gameDTO);
//        }
//    }
}
