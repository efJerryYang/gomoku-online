package me.efjerryyang.gomokuonline.controller;

import io.jsonwebtoken.JwtException;
import me.efjerryyang.gomokuonline.Constant;
import me.efjerryyang.gomokuonline.dto.CheckGameDTO;
import me.efjerryyang.gomokuonline.dto.GameDTO;
import me.efjerryyang.gomokuonline.dto.MoveDTO;
import me.efjerryyang.gomokuonline.dto.NewRoundDTO;
import me.efjerryyang.gomokuonline.entity.Game;
import me.efjerryyang.gomokuonline.entity.Move;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private GameService gameService;

    @PostMapping("/move")
    public ResponseEntity<GameDTO> move(@RequestHeader("Authorization") String token, @RequestBody MoveDTO moveDTO) {
        logger.info("POST /api/move (moveDTO: " + moveDTO + ")");
        try {
            if (moveDTO == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            String clientId = jwtService.getClientIdFromToken(token);
            User user = userService.getUserByClientId(clientId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Game game = gameService.getGameById(moveDTO.getGameId());
            if (game.getStatus() != Constant.GAME_STATUS_PLAYING) {
                // if not playing, cannot move
                switch (game.getStatus()) {
                    case Constant.GAME_STATUS_PENDING -> logger.info("Game is waiting for another player");
                    case Constant.GAME_STATUS_PLAYER1_WIN -> logger.info("Game is over, player 1 win");
                    case Constant.GAME_STATUS_PLAYER2_WIN -> logger.info("Game is over, player 2 win");
                    case Constant.GAME_STATUS_IT_IS_A_TIE -> logger.info("Game is over, it is a tie");
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Player player = game.getPlayerByPlayerId(user.getId());
            Move move = new Move(player, moveDTO);
            // update game
            gameService.updateGameMove(game, move);
            // return updated game board.
            logger.info("Player 1: " + game.getPlayer1().getScore() + " Player 2: " + game.getPlayer2().getScore());
            return ResponseEntity.ok().body(new GameDTO(game));
        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/game")
    public ResponseEntity<GameDTO> getGame(@RequestHeader("Authorization") String token, @RequestBody CheckGameDTO checkGameDTO) {
        logger.info("POST /api/game (checkGameDTO: " + checkGameDTO + ")");
        try {
            String clientId = jwtService.getClientIdFromToken(token);
            User user = userService.getUserByClientId(clientId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Game game = gameService.getGameById(checkGameDTO.getGameId());
            if (game == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            GameDTO gameDTO = new GameDTO(game);
            Player player = game.getPlayerByPlayerId(user.getId());
            if (game.getStatus() == Constant.GAME_STATUS_PLAYING && gameDTO.getWhoseTurn().equals(user.getId())) {
                // if it is the user's turn, check if his turn is timed out
                Integer gameStatus = gameService.checkTimeout(game, System.currentTimeMillis(), player);
                game.setStatus(gameStatus);
                gameService.updateScores(game, gameStatus);
            }

            switch (game.getStatus()) {
                case Constant.GAME_STATUS_PENDING -> logger.info("Game is waiting for another player");
                case Constant.GAME_STATUS_PLAYING -> logger.info("Game is playing");
                case Constant.GAME_STATUS_PLAYER1_WIN, Constant.GAME_STATUS_PLAYER2_WIN, Constant.GAME_STATUS_IT_IS_A_TIE -> {
                    logger.info("Game is over");
                    // try finding another pending game
                    Game newGame = gameService.findAndJoinPendingGame(player);
                    logger.info("New game: " + newGame);
                    return ResponseEntity.ok().body(new GameDTO(Objects.requireNonNullElse(newGame, game)));
                }
                case Constant.GAME_EXIT -> {
                    return ResponseEntity.ok().build();
                }
                default -> {
                    logger.warn("Game status is not valid: " + game.getStatus());
                }
            }
            return ResponseEntity.ok().body(new GameDTO(game));
        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/game/new")
    public ResponseEntity<GameDTO> newGame(@RequestHeader("Authorization") String token, @RequestBody NewRoundDTO newRoundDTO) {
        logger.info("POST /api/game/new (newRoundDTO: " + newRoundDTO + ")");
        try {
            String clientId = jwtService.getClientIdFromToken(token);
            User user = userService.getUserByClientId(clientId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Game game = gameService.getGameById(newRoundDTO.getGameId());
            Game newGame = gameService.createGame(game.getPlayer1(), game.getPlayer2(), game.getWhoFirst());
            gameService.addGame(newGame);
            return ResponseEntity.ok().body(new GameDTO(newGame));
        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/game/exit")
    public ResponseEntity<GameDTO> exitGame(@RequestHeader("Authorization") String token, @RequestBody CheckGameDTO checkGameDTO) {
        logger.info("POST /api/game/exit (checkGameDTO: " + checkGameDTO + ")");
        try {
            String clientId = jwtService.getClientIdFromToken(token);
            User user = userService.getUserByClientId(clientId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Game game = gameService.getGameById(checkGameDTO.getGameId());
            if (game == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            gameService.exitGame(game);
            return ResponseEntity.ok().build();
        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
