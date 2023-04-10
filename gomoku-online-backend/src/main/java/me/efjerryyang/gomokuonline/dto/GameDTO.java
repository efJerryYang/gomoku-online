package me.efjerryyang.gomokuonline.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDTO {
    private Integer id;
    private String player1;
    private String player2;
    private Integer turn;
    private Integer[][] board;
    private Integer result;
}
