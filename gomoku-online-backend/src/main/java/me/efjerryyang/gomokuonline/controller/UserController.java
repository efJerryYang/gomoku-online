package me.efjerryyang.gomokuonline.controller;

import me.efjerryyang.gomokuonline.dto.MatchGetDTO;
import me.efjerryyang.gomokuonline.dto.PickDTO;
import me.efjerryyang.gomokuonline.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/pick")
    public ResponseEntity<String> pickUsername(@RequestBody PickDTO pickDTO) {
        if (pickDTO == null || pickDTO.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please enter a username");

        }

        String username = pickDTO.getUsername().trim();
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
