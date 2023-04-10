package me.efjerryyang.gomokuonline.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Move {
    private Integer x;
    private Integer y;
    private Player player;
    private Long moveTime;
}
