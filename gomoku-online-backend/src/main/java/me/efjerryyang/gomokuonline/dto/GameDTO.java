package me.efjerryyang.gomokuonline.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.efjerryyang.gomokuonline.entity.Game;
import me.efjerryyang.gomokuonline.entity.Player;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDTO {
    private Long id;
    private Player player1;
    private Player player2;
    private Integer turn;
    private Integer[][] board;
    private Integer result;

    public GameDTO(Game game) {
        this.id = game.getId();
        this.player1 = game.getPlayer1();
        this.player2 = game.getPlayer2();
        this.turn = game.getTurn();
        this.board = game.getBoard();
        this.result = game.getStatus();
    }
}
