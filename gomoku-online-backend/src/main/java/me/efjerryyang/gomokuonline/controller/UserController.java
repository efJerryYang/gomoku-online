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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private GameService gameService;

    @PostMapping("/pick")
    public ResponseEntity<Object> pickUsername(@RequestHeader("Authorization") String token, @RequestBody PickDTO pickDTO) {
        logger.info("POST /api/pick (pickDTO=" + pickDTO + ")");
        try {
            String clientId = jwtService.getClientIdFromToken(token);
            logger.info("Token: " + token);
            logger.info("Client ID: " + clientId);
            if (pickDTO == null || pickDTO.getUsername() == null) {
                logger.warn("Invalid PickDTO or Username.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponsePick(null, "Username cannot be null"));
            }
            String username = pickDTO.getUsername().trim();
            logger.info("Username: " + username);
            if (username.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponsePick(null, "Username cannot be all spaces"));
            }
            User newUser = userService.pickUsername(username, clientId);
            logger.info("New User: " + newUser);
            if (newUser != null) {
                userService.updateWaitingList(newUser);
                logger.debug("User added to waiting list: " + newUser);
                return ResponseEntity.ok().body(new ResponsePick(newUser.getId(), "Successfully joined the matching list"));
            } else {
                logger.warn("Failed to join the matching list: " + newUser);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponsePick(null, "Failed to join the matching list"));
            }

        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Invalid token: " + e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponsePick(null, "Invalid token"));
        }
    }

    // TODO: Offline user detection
    @GetMapping("/match")
    public ResponseEntity<List<MatchGetDTO>> getWaitingList() {
        List<MatchGetDTO> matchGetDTOList = userService.getWaitingList();
        logger.debug("GET: /api/match (matchGetDTOList=" + matchGetDTOList + ")");
        return ResponseEntity.ok(matchGetDTOList);
    }

    @GetMapping("/matching")
    public ResponseEntity<GameDTO> getMatchingStatus(@RequestHeader("Authorization") String token) {
        String clientId = jwtService.getClientIdFromToken(token);
        logger.info("GET: /api/matching (clientId=" + clientId + ")");
        User user = userService.getUserByClientId(clientId);
        logger.info("Authorized User: " + user);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        // Get game by user id
        Game game = gameService.getGameByPlayerId(user.getId());
        logger.info("Current game of the user: " + game);
        if (game == null) {
            return ResponseEntity.ok(new GameDTO());
        }
        Player opponent = game.getOpponentByPlayerId(user.getId());
        logger.info("Opponent of the user: " + opponent);
        if (opponent == null) {
            return ResponseEntity.ok(new GameDTO());
        } else {
            game.setRoundCreatedTime(System.currentTimeMillis());
            game.setStatus(Constant.GAME_STATUS_PLAYING);
            logger.info("Game status changed to playing: " + game);
            return ResponseEntity.ok(new GameDTO(game));
        }
    }

    // TODO: current possibility of duplicated userid or gameid
    @PostMapping("/match")
    public ResponseEntity<GameDTO> matchWithPlayer(@RequestBody MatchPostDTO matchDTO) {
        logger.info("POST /api/match (userId=" + matchDTO.getUserId() + ", opponentId=" + matchDTO.getOpponentId() + ")");
        if (matchDTO.getUserId() == null || matchDTO.getOpponentId() == null || matchDTO.getUserId().equals(matchDTO.getOpponentId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Game game = gameService.getGameByPlayerId(matchDTO.getUserId()); // What does this use for?
        logger.info("Current game of the given user: " + game);
        if (game != null && game.getStatus().equals(Constant.GAME_STATUS_PENDING)) {
            // another player checked the matching status first
            game.setRoundCreatedTime(System.currentTimeMillis());
            game.setStatus(Constant.GAME_STATUS_PLAYING);
            logger.info("Game status changed to playing: " + game);
            return ResponseEntity.ok(new GameDTO(game));
        } else if (game != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        game = userService.matchPlayers(matchDTO.getUserId(), matchDTO.getOpponentId());
        gameService.addGame(game);
        logger.info("New Game:" + game);
        if (game == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } else {
            return ResponseEntity.ok(new GameDTO(game));
        }
    }

    @PostMapping("/match/dice")
    public ResponseEntity<GameDTO> matchWithRandomPlayer(@RequestBody DiceDTO diceDTO) {
        logger.info("POST /api/match/dice (userId=" + diceDTO.getUserId() + ")");
        if (diceDTO.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Game game = gameService.getGameByPlayerId(diceDTO.getUserId()); // What does this use for?
        logger.info("Current game of the given user: " + game);
        if (game != null && game.getStatus().equals(Constant.GAME_STATUS_PENDING)) {
            game.setRoundCreatedTime(System.currentTimeMillis());
            game.setStatus(Constant.GAME_STATUS_PLAYING);
            return ResponseEntity.ok(new GameDTO(game));
        }
        game = userService.matchWithRandomPlayer(diceDTO.getUserId());
        gameService.addGame(game);
        logger.info("New game" + game);
        if (game == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.ok(new GameDTO(game));
        }
    }

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
