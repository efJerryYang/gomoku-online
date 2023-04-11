package me.efjerryyang.gomokuonline.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.efjerryyang.gomokuonline.dto.MoveDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Move {
    private Integer x;
    private Integer y;
    private Player player;
    private Long moveTime;

    public Move(Player player, MoveDTO moveDTO) {
        this.x = moveDTO.getX();
        this.y = moveDTO.getY();
        this.player = player;
        this.moveTime = moveDTO.getMoveTime();
    }
}
