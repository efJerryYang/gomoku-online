package me.efjerryyang.gomokuonline.controller;

import io.jsonwebtoken.JwtException;
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
import org.springframework.web.bind.annotation.*;

@RestController
public class GameController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private GameService gameService;

    @PostMapping("/move")
    public ResponseEntity<GameDTO> move(@RequestHeader("Authorization") String token, @RequestBody  MoveDTO moveDTO) {
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
            Player player = game.getPlayerByPlayerId(user.getId());
            Move move = new Move(player, moveDTO);
            System.out.println("Player " + player.getUsername() + " move to " + move.getX() + ", " + move.getY());
            // update game
            gameService.updateGameMove(game, move);
            // return updated game board.
            return ResponseEntity.ok().body(new GameDTO(game));
        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
