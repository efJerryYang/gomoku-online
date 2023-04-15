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
    private Long whoseTurn;
    private Integer[][] board;
    private Integer result;
    private Integer player1Stone;
    private Integer player2Stone;

    public GameDTO(Game game) {
        this.id = game.getId();
        this.player1 = game.getPlayer1();
        this.player2 = game.getPlayer2();
        this.turn = game.getTurn();
        this.whoseTurn = game.getTurn() % 2 == (game.getWhoFirst() % 2) ? player1.getId() : player2.getId();
        this.board = game.getBoard();
        this.result = game.getStatus();
        this.player1Stone = game.getPlayerStoneType(player1);
        this.player2Stone = game.getPlayerStoneType(player2);
    }
}
