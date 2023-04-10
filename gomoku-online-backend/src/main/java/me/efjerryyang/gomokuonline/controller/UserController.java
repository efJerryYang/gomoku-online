package me.efjerryyang.gomokuonline.controller;

import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.efjerryyang.gomokuonline.dto.GameDTO;
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
    public ResponseEntity<Object> pickUsername(@RequestHeader("Authorization") String token, @RequestBody PickDTO pickDTO) {
        try {
            String clientId = jwtService.getClientIdFromToken(token);
            if (pickDTO == null || pickDTO.getUsername() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponsePick(null, "Username cannot be null"));

            }
            String username = pickDTO.getUsername().trim();
            if (username.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponsePick(null, "Username cannot be all spaces"));
            }
            User newUser = userService.pickUsername(username, clientId);
            if (newUser != null) {
                userService.updateWaitingList(newUser);
                return ResponseEntity.ok().body(new ResponsePick(newUser.getId(), "Successfully joined the matching list"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponsePick(null, "Failed to join the matching list"));
            }

        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponsePick(null, "Invalid token"));
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class ResponsePick {
        private Long id;
        private String info;
    }

    // TODO: Offline user detection
    @GetMapping("/match")
    public ResponseEntity<List<MatchGetDTO>> getWaitingList() {
        List<MatchGetDTO> matchGetDTOList = userService.getWaitingList();
        return ResponseEntity.ok(matchGetDTOList);
    }

//    @PostMapping("/match")
//    public ResponseEntity<GameDTO> matchWithPlayer(@RequestBody MatchPostDTO matchDTO) {
//        GameDTO gameDTO = userService.matchWithPlayer(matchDTO.getId(), matchDTO.getOpponentId());
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
