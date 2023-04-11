package me.efjerryyang.gomokuonline.service;

import me.efjerryyang.gomokuonline.Constant;
import me.efjerryyang.gomokuonline.entity.Game;
import me.efjerryyang.gomokuonline.entity.Move;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
public class GameService {
    private final List<Game> gameList;

    public GameService() {
        this.gameList = new LinkedList<>();
    }

    public void addGame(Game game) {
        gameList.add(game);
    }

    public Game getGameById(Long id) {
        return gameList.stream().filter(game -> game.getId().equals(id)).findFirst().orElse(null);
    }

    public void removeGameById(Long id) {
        gameList.removeIf(game -> game.getId().equals(id));
    }

    public Game getGameByPlayerId(Long id) {
        return gameList.stream().filter(game -> game.getPlayer1().getId().equals(id) || game.getPlayer2().getId().equals(id)).findFirst().orElse(null);
    }

    public void updateGameStatus(Long id, Integer status) {
        gameList.stream().filter(game -> game.getId().equals(id)).findFirst().ifPresent(game -> game.setStatus(status));
    }

    public void updateGameMove(Game game, Move move) {
        // validate the move
        if (game.getBoard()[move.getX()][move.getY()] != 0) {
            return;
        }
        // check if the player's turn
        if (!game.getNowTurnPlayer().equals(move.getPlayer())) {
            return;
        }
        // update game move
        game.getMoves().add(move);
        game.getBoard()[move.getX()][move.getY()] = game.getPlayerStoneType(move.getPlayer());
        // check if game end
        Integer gameStatus = checkGameStatus(game);
        game.setTurn(game.getTurn() + 1);

        if (gameStatus != Constant.GAME_STATUS_PLAYING) {
            game.setStatus(gameStatus);
            System.out.print("Game end: ");
            switch (gameStatus) {
                case Constant.GAME_STATUS_PLAYER1_WIN ->
                        System.out.println("Player 1 win (" + game.getPlayer1().getUsername() + ")");
                case Constant.GAME_STATUS_PLAYER2_WIN ->
                        System.out.println("Player 2 win (" + game.getPlayer2().getUsername() + ")");
                case Constant.GAME_STATUS_IT_IS_A_TIE -> System.out.println("It is a tie");
            }
        }
    }

    public Integer checkGameStatus(Game game) {
        Integer[][] board = game.getBoard();
        Integer turn = game.getTurn();
        Integer whoFirst = game.getWhoFirst();
        Integer playerNumber = game.getNowTurnPlayerNumber();
        Integer playerStone = game.getPlayerStoneType(game.getPlayer(playerNumber));
        for (int i = 0; i < Constant.BOARD_SIZE; i++) {
            for (int j = 0; j < Constant.BOARD_SIZE; j++) {
                if (Objects.equals(board[i][j], playerStone)) {
                    if (i + 4 < Constant.BOARD_SIZE && board[i + 1][j] == playerStone && board[i + 2][j] == playerStone && board[i + 3][j] == playerStone && board[i + 4][j] == playerStone) {
                        return playerNumber + Constant.GAME_STATUS_IT_IS_A_TIE;
                    }
                    if (j + 4 < Constant.BOARD_SIZE && board[i][j + 1] == playerStone && board[i][j + 2] == playerStone && board[i][j + 3] == playerStone && board[i][j + 4] == playerStone) {
                        return playerNumber + Constant.GAME_STATUS_IT_IS_A_TIE;
                    }
                    if (i + 4 < Constant.BOARD_SIZE && j + 4 < Constant.BOARD_SIZE && board[i + 1][j + 1] == playerStone && board[i + 2][j + 2] == playerStone && board[i + 3][j + 3] == playerStone && board[i + 4][j + 4] == playerStone) {
                        return playerNumber + Constant.GAME_STATUS_IT_IS_A_TIE;
                    }
                    if (i - 4 >= 0 && j + 4 < Constant.BOARD_SIZE && board[i - 1][j + 1] == playerStone && board[i - 2][j + 2] == playerStone && board[i - 3][j + 3] == playerStone && board[i - 4][j + 4] == playerStone) {
                        return playerNumber + Constant.GAME_STATUS_IT_IS_A_TIE;
                    }
                }
            }
        }
        if (turn == Constant.BOARD_SIZE * Constant.BOARD_SIZE) {
            return Constant.GAME_STATUS_IT_IS_A_TIE;
        }
        return Constant.GAME_STATUS_PLAYING;
    }
//    // Dynamic Programming attempts
//    public Integer checkGameStatus(Game game) {
//        Integer[][] board = game.getBoard();
//        int[][][] dp = new int[Constant.BOARD_SIZE][Constant.BOARD_SIZE][6];
//        for (int i = 0; i < Constant.BOARD_SIZE; i++) {
//            for (int j = 0; j < Constant.BOARD_SIZE; j++) {
//                Arrays.fill(dp[i][j], 0);
//            }
//        }
//
//        int lastX = -1, lastY = -1;
//        for (int i = 0; i < Constant.BOARD_SIZE; i++) {
//            for (int j = 0; j < Constant.BOARD_SIZE; j++) {
//                if (!Objects.equals(board[i][j], null)) {
//                    int color = board[i][j];
//                    for (int k = 1; k <= 5; k++) {
//                        if (i >= k - 1 && dp[i - (k - 1)][j][k - 1] > 0) {
//                            dp[i][j][k] = dp[i - (k - 1)][j][k - 1] + 1;
//                        }
//                        if (j >= k - 1 && dp[i][j - (k - 1)][k - 1] > 0) {
//                            dp[i][j][k] = dp[i][j - (k - 1)][k - 1] + 1;
//                        }
//                        if (i >= k - 1 && j >= k - 1 && dp[i - (k - 1)][j - (k - 1)][k - 1] > 0) {
//                            dp[i][j][k] = dp[i - (k - 1)][j - (k - 1)][k - 1] + 1;
//                        }
//                        if (i >= k - 1 && j <= Constant.BOARD_SIZE - k && dp[i - (k - 1)][j + (k - 1)][k - 1] > 0) {
//                            dp[i][j][k] = dp[i - (k - 1)][j + (k - 1)][k - 1] + 1;
//                        }
//                        if (dp[i][j][k] >= k) {
//                            if (lastX == -1 || dp[lastX][lastY][k] < dp[i][j][k] || (dp[lastX][lastY][k] == dp[i][j][k] && board[lastX][lastY] != color)) {
//                                lastX = i;
//                                lastY = j;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        if (lastX != -1 && dp[lastX][lastY][5] >= 5) {
//            return board[lastX][lastY] + Constant.GAME_STATUS_IT_IS_A_TIE;
//        } else if (game.getTurn() == Constant.BOARD_SIZE * Constant.BOARD_SIZE) {
//            return Constant.GAME_STATUS_IT_IS_A_TIE;
//        } else {
//            return Constant.GAME_STATUS_PLAYING;
//        }
//    }

}
