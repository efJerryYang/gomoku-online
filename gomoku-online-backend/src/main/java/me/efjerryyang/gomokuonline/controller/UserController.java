package me.efjerryyang.gomokuonline.controller;

import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.efjerryyang.gomokuonline.Constant;
import me.efjerryyang.gomokuonline.dto.*;
import me.efjerryyang.gomokuonline.entity.Game;
import me.efjerryyang.gomokuonline.entity.Player;
import me.efjerryyang.gomokuonline.entity.User;
import me.efjerryyang.gomokuonline.service.GameService;
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

    @Autowired
    private GameService gameService;

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

    // TODO: Offline user detection
    @GetMapping("/match")
    public ResponseEntity<List<MatchGetDTO>> getWaitingList() {
        List<MatchGetDTO> matchGetDTOList = userService.getWaitingList();
        return ResponseEntity.ok(matchGetDTOList);
    }

    @GetMapping("/matching")
    public ResponseEntity<GameDTO> getMatchingStatus(@RequestHeader("Authorization") String token) {
        String clientId = jwtService.getClientIdFromToken(token);
//        System.out.println("GET: matching (clientId=" + clientId + ")");
        User user = userService.getUserByClientId(clientId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        // Get game by user id
        Game game = gameService.getGameByPlayerId(user.getId());
        if (game == null) {
            return ResponseEntity.ok(new GameDTO());
        }
        Player opponent = game.getOpponentByPlayerId(user.getId());
        if (opponent == null) {
            return ResponseEntity.ok(new GameDTO());
        } else {
            game.setStatus(Constant.GAME_STATUS_PLAYING);
            return ResponseEntity.ok(new GameDTO(game));
        }
    }

    // TODO: current possibility of duplicated userid or gameid
    @PostMapping("/match")
    public ResponseEntity<GameDTO> matchWithPlayer(@RequestBody MatchPostDTO matchDTO) {
        if (matchDTO.getUserId() == null || matchDTO.getOpponentId() == null || matchDTO.getUserId().equals(matchDTO.getOpponentId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Game game = gameService.getGameByPlayerId(matchDTO.getUserId()); // What does this use for?
        if (game != null && game.getStatus().equals(Constant.GAME_STATUS_PENDING)) {
            // another player checked the matching status first
            game.setStatus(Constant.GAME_STATUS_PLAYING);
            return ResponseEntity.ok(new GameDTO(game));
        } else if (game != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        game = userService.matchPlayers(matchDTO.getUserId(), matchDTO.getOpponentId());
        gameService.addGame(game);
        if (game == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } else {
            return ResponseEntity.ok(new GameDTO(game));
        }
    }

    @PostMapping("/match/dice")
    public ResponseEntity<GameDTO> matchWithRandomPlayer(@RequestBody DiceDTO diceDTO) {
        if (diceDTO.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Game game = gameService.getGameByPlayerId(diceDTO.getUserId()); // What does this use for?
        if (game!= null && game.getStatus().equals(Constant.GAME_STATUS_PENDING)){
            game.setStatus(Constant.GAME_STATUS_PLAYING);
            return ResponseEntity.ok(new GameDTO(game));
        }
        game = userService.matchWithRandomPlayer(diceDTO.getUserId());
        if (game == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.ok(new GameDTO(game));
        }
    }

//    @PostMapping("/matchConfirm")
//    public ResponseEntity<GameDTO> confirmMatch(@RequestHeader("Authorization") String token, @RequestBody MatchConfirmDTO matchDTO) {
//        String clientId = jwtService.getClientIdFromToken(token);
//        Long userId = userService.getUserByClientId(clientId).getId();
//        System.out.println("POST: matchConfirm (clientId=" + clientId + ")");
//        if (matchDTO.getResult() == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//        Game game = gameService.getGameByPlayerId(userId);
//        if (game != null && game.getStatus().equals(Constant.GAME_STATUS_PENDING)) {
//            // another player checked the matching status first
//            gameService.updateGameStatus(game.getId(), Constant.GAME_STATUS_PLAYING);
//            return ResponseEntity.ok(new GameDTO(game));
//        } else if (game != null) {
//            return ResponseEntity.ok(new GameDTO(game));
//        } else {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class ResponsePick {
        private Long id;
        private String info;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class ResponseMatching {
        private Player matchedPlayer;
        private String info;
    }
}
