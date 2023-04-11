package me.efjerryyang.gomokuonline.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    private Long id;
    private Player player1;
    private Player player2;
    private Integer turn;
    private Integer[][] board;
    private Integer status;
    private Integer whoFirst;
    private List<Move> moves;

    public Player getOpponentByPlayerId(Long playerId) {
        if (Objects.equals(playerId, player1.getId())) {
            return player2;
        } else {
            return player1;
        }
    }

    public Player getOpponent(Player player) {
        if (Objects.equals(player.getId(), player1.getId())) {
            return player2;
        } else {
            return player1;
        }
    }

}
