package me.efjerryyang.gomokuonline.controller;

import me.efjerryyang.gomokuonline.dto.GameDTO;
import me.efjerryyang.gomokuonline.dto.MatchDTO;
import me.efjerryyang.gomokuonline.dto.UserDTO;
import me.efjerryyang.gomokuonline.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/pick")
    public ResponseEntity<String> pickUsername(@RequestBody UserDTO userDTO) {
        if (userDTO == null || userDTO.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please enter a username");

        }

        String username = userDTO.getUsername().trim();
        if (username.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username cannot be all spaces");
        }


        boolean success = userService.pickUsername(username);
        if (success) {
            return ResponseEntity.ok("Successfully joined the matching list");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to join the matching list");
        }

    }
//
//    @PostMapping("/match")
//    public ResponseEntity<GameDTO> matchWithPlayer(@RequestBody MatchDTO matchDTO) {
//        GameDTO gameDTO = userService.matchWithPlayer(matchDTO.getUsername(), matchDTO.getOpponentId());
//        if (gameDTO == null) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        } else {
//            return ResponseEntity.ok(gameDTO);
//        }
//    }
//
//    @PostMapping("/match/dice")
//    public ResponseEntity<GameDTO> matchWithRandomPlayer(@RequestBody UserDTO userDTO) {
//        GameDTO gameDTO = userService.matchWithRandomPlayer(userDTO.getUsername());
//        if (gameDTO == null) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        } else {
//            return ResponseEntity.ok(gameDTO);
//        }
//    }
}
