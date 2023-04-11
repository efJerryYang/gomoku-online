package me.efjerryyang.gomokuonline.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.efjerryyang.gomokuonline.entity.Move;
import me.efjerryyang.gomokuonline.entity.Player;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoveDTO {
    private Long gameId;
    private Integer x;
    private Integer y;
    private Long moveTime;

    public MoveDTO(Long gameId, Move move) {
        this.gameId = gameId;
        this.x = move.getX();
        this.y = move.getY();
        this.moveTime = move.getMoveTime();
    }
}
