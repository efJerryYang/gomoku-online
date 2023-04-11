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
        if (game.getTurn() % 2 != (game.getWhoFirst() % 2) && game.getPlayer1().getId().equals(move.getPlayer().getId())) {
            return;
        }
        gameList.stream().filter(game1 -> game1.getId().equals(game.getId())).findFirst().ifPresent(game1 -> {
            game1.getMoves().add(move);
            game1.setTurn(game1.getTurn() + 1);
            game1.getBoard()[move.getX()][move.getY()] = game1.getTurn() % 2 == (game1.getWhoFirst() % 2) ? 1 : 2;
        });
        // check if game end
        Integer gameStatus = checkGameStatus(game);
        if (gameStatus != Constant.GAME_STATUS_PLAYING) {
            game.setStatus(gameStatus);
        }
        // update game move
        game.getMoves().add(move);
//        game.setTurn(game.getTurn() + 1);
    }

    public Integer checkGameStatus(Game game) {
        Integer[][] board = game.getBoard();
        Integer turn = game.getTurn();
        Integer whoFirst = game.getWhoFirst();
        Integer player = turn % 2 == (whoFirst % 2) ? 1 : 2;
        for (int i = 0; i < Constant.BOARD_SIZE; i++) {
            for (int j = 0; j < Constant.BOARD_SIZE; j++) {
                if (Objects.equals(board[i][j], player)) {
                    if (i + 4 < Constant.BOARD_SIZE && board[i + 1][j] == player && board[i + 2][j] == player && board[i + 3][j] == player && board[i + 4][j] == player) {
                        return player + Constant.GAME_STATUS_IT_IS_A_TIE;
                    }
                    if (j + 4 < Constant.BOARD_SIZE && board[i][j + 1] == player && board[i][j + 2] == player && board[i][j + 3] == player && board[i][j + 4] == player) {
                        return player + Constant.GAME_STATUS_IT_IS_A_TIE;
                    }
                    if (i + 4 < Constant.BOARD_SIZE && j + 4 < Constant.BOARD_SIZE && board[i + 1][j + 1] == player && board[i + 2][j + 2] == player && board[i + 3][j + 3] == player && board[i + 4][j + 4] == player) {
                        return player + Constant.GAME_STATUS_IT_IS_A_TIE;
                    }
                    if (i - 4 >= 0 && j + 4 < Constant.BOARD_SIZE && board[i - 1][j + 1] == player && board[i - 2][j + 2] == player && board[i - 3][j + 3] == player && board[i - 4][j + 4] == player) {
                        return player + Constant.GAME_STATUS_IT_IS_A_TIE;
                    }
                }
            }
        }
        if (turn == Constant.BOARD_SIZE * Constant.BOARD_SIZE) {
            return Constant.GAME_STATUS_IT_IS_A_TIE;
        }
        return Constant.GAME_STATUS_PLAYING;
    }
}
