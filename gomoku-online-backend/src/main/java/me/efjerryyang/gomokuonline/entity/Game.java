package me.efjerryyang.gomokuonline.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.efjerryyang.gomokuonline.Constant;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Game {
    private Long id;
    private Player player1;
    private Player player2;
    private Integer turn;
    private Integer[][] board;
    private Integer status;
    private Integer whoFirst;
    private List<Move> moves;
    private Long roundCreatedTime;

    public Player getOpponentByPlayerId(Long playerId) {
        if (Objects.equals(playerId, player1.getId())) {
            return player2;
        } else {
            return player1;
        }
    }

    public Player getPlayer(Integer playerNumber) {
        if (Objects.equals(playerNumber, 1)) {
            return player1;
        } else {
            return player2;
        }
    }

    public Integer getNowTurnPlayerNumber() {
        return turn % 2 == (whoFirst % 2) ? 1 : 2;
    }

    public Integer getPlayerStoneType(Player player) {
        return player.equals(getPlayer(getWhoFirst())) ? Constant.BLACK_CELL : Constant.WHITE_CELL;
    }

    public Player getNowTurnPlayer() {
        return getPlayer(getNowTurnPlayerNumber());
    }

    public Player getPlayerByPlayerId(Long playerId) {
        if (Objects.equals(playerId, player1.getId())) {
            return player1;
        } else {
            return player2;
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
