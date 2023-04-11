package me.efjerryyang.gomokuonline.controller;

import io.jsonwebtoken.JwtException;
import me.efjerryyang.gomokuonline.Constant;
import me.efjerryyang.gomokuonline.dto.CheckGameDTO;
import me.efjerryyang.gomokuonline.dto.GameDTO;
import me.efjerryyang.gomokuonline.dto.MoveDTO;
import me.efjerryyang.gomokuonline.entity.Game;
import me.efjerryyang.gomokuonline.entity.Move;
import me.efjerryyang.gomokuonline.entity.Player;
import me.efjerryyang.gomokuonline.entity.User;
import me.efjerryyang.gomokuonline.service.GameService;
import me.efjerryyang.gomokuonline.service.JwtService;
import me.efjerryyang.gomokuonline.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private GameService gameService;

    @PostMapping("/move")
    public ResponseEntity<GameDTO> move(@RequestHeader("Authorization") String token, @RequestBody MoveDTO moveDTO) {
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
                // TODO: add error message for this
                switch (game.getStatus()) {
                    case Constant.GAME_STATUS_PENDING -> System.out.println("Game is waiting for another player");
                    case Constant.GAME_STATUS_PLAYER1_WIN -> System.out.println("Game is over, player 1 win");
                    case Constant.GAME_STATUS_PLAYER2_WIN -> System.out.println("Game is over, player 2 win");
                    case Constant.GAME_STATUS_IT_IS_A_TIE -> System.out.println("Game is over, it is a tie");
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Player player = game.getPlayerByPlayerId(user.getId());
            Move move = new Move(player, moveDTO);
//            if (game.getMoves().get(game.getMoves().size() - 1).getPlayer().getId().equals(player.getId())) {
//                // if last move is from the same player, cannot move
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//            }
            // update game
            gameService.updateGameMove(game, move);
            System.out.println("(turn=" + game.getTurn() + ") " + "Player " + player.getUsername() + " move to " + move.getX() + ", " + move.getY());
            // return updated game board.
            return ResponseEntity.ok().body(new GameDTO(game));
        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/game")
    public ResponseEntity<GameDTO> getGame(@RequestHeader("Authorization") String token, @RequestBody CheckGameDTO checkGameDTO) {
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
            return ResponseEntity.ok().body(new GameDTO(game));
        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
