package me.efjerryyang.gomokuonline.service;

import me.efjerryyang.gomokuonline.Constant;
import me.efjerryyang.gomokuonline.entity.Game;
import me.efjerryyang.gomokuonline.entity.Move;
import me.efjerryyang.gomokuonline.entity.Player;
import me.efjerryyang.gomokuonline.entity.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {
    private final List<Game> gameList;

    public GameService() {
        this.gameList = new LinkedList<>();
    }

    public void addGame(Game game) {
        gameList.add(game);
    }

    public List<Game> getGameList() {
        return gameList;
    }

    public Game findAndJoinPendingGame(Player player) {
        // try finding pending game for player
        // namely, either player1 or player2 should be player
        Game game = gameList.stream()
                .filter(g -> g.getStatus().equals(Constant.GAME_STATUS_PENDING))
                .filter(g -> g.getPlayer1().getId().equals(player.getId()) || g.getPlayer2().getId().equals(player.getId()))
                .findFirst()
                .orElse(null);
        if (game != null) {
            game.setStatus(Constant.GAME_STATUS_PLAYING);
        }
        return game;
    }

    public Game createGame(User user1, User user2) {
        Game game = new Game();
        game.setId((long) (Math.random() * 1_0000_0000));
        game.setPlayer1(new Player(user1.getUsername(), user1.getId(), 0));
        game.setPlayer2(new Player(user2.getUsername(), user2.getId(), 0));
        game.setTurn(1);

        // game.setBoard(new Integer[Constant.BOARD_SIZE][Constant.BOARD_SIZE]);
        Integer zero = Constant.BACKGROUND_CELL;
        Integer[][] board = new Integer[Constant.BOARD_SIZE][Constant.BOARD_SIZE];
        for (Integer[] row : board) {
            Arrays.fill(row, zero);
        }
        game.setBoard(board);
        game.setStatus(Constant.GAME_STATUS_PENDING);
        game.setWhoFirst((int) (Math.random() * 2) + 1); // (int) [1.0, 3.0) => 1 or 2
        switch (game.getWhoFirst()) {
            case 1 -> System.out.println("Player " + game.getPlayer1().getUsername() + " goes first");
            case 2 -> System.out.println("Player " + game.getPlayer2().getUsername() + " goes first");
        }
        game.setMoves(new ArrayList<>());
        return game;
    }

    public Game createGame(Player player1, Player player2, Integer whoFirst) {
        Game game = new Game();
        game.setId((long) (Math.random() * 1_0000_0000));

        game.setPlayer1(player1);
        game.setPlayer2(player2);

        game.setTurn(1);
        Integer zero = Constant.BACKGROUND_CELL;
        Integer[][] board = new Integer[Constant.BOARD_SIZE][Constant.BOARD_SIZE];
        for (Integer[] row : board) {
            Arrays.fill(row, zero);
        }
        game.setBoard(board);
        game.setStatus(Constant.GAME_STATUS_PENDING);

        game.setWhoFirst(whoFirst == 1 ? 2 : 1);

        game.setMoves(new ArrayList<>());
        return game;
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
        System.out.println("(turn=" + game.getTurn() + ") " + "Player " + move.getPlayer().getUsername() + " move to " + move.getX() + ", " + move.getY());
        // check if game end
        Integer gameStatus = checkGameStatus(game);
        game.setTurn(game.getTurn() + 1);

        if (gameStatus != Constant.GAME_STATUS_PLAYING) {
            game.setStatus(gameStatus);
            System.out.print("Game end: ");
            switch (gameStatus) {
                case Constant.GAME_STATUS_PLAYER1_WIN -> {
                    game.getPlayer1().setScore(game.getPlayer1().getScore() + 1);
                    System.out.println("Player 1 win (" + game.getPlayer1().getUsername() + ")");
                }
                case Constant.GAME_STATUS_PLAYER2_WIN -> {
                    game.getPlayer2().setScore(game.getPlayer2().getScore() + 1);
                    System.out.println("Player 2 win (" + game.getPlayer2().getUsername() + ")");
                }
                case Constant.GAME_STATUS_IT_IS_A_TIE -> {
                    game.getPlayer1().setScore(game.getPlayer1().getScore() + 1);
                    game.getPlayer2().setScore(game.getPlayer2().getScore() + 1);
                    System.out.println("It is a tie");
                }
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

    public void exitGame(Game game) {
        game.setStatus(Constant.GAME_EXIT);
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
