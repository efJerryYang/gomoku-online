package me.efjerryyang.gomokuonline.service;

import me.efjerryyang.gomokuonline.Constant;
import me.efjerryyang.gomokuonline.entity.Game;
import me.efjerryyang.gomokuonline.entity.Move;
import me.efjerryyang.gomokuonline.entity.Player;
import me.efjerryyang.gomokuonline.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    private final List<Game> gameList;

    public GameService() {
        logger.info("GameService initialized");
        this.gameList = new LinkedList<>();
    }

    public void addGame(Game game) {
        logger.info("addGame: " + game);
        gameList.add(game);
    }

    public List<Game> getGameList() {
        logger.info("getGameList: " + gameList);
        return gameList;
    }

    public Game findAndJoinPendingGame(Player player) {
        logger.info("findAndJoinPendingGame: " + player);
        // Try finding pending game for player
        // namely, either player1 or player2 should be player
        Game game = gameList.stream()
                .filter(g -> g.getStatus().equals(Constant.GAME_STATUS_PENDING))
                .filter(g -> g.getPlayer1().getId().equals(player.getId()) || g.getPlayer2().getId().equals(player.getId()))
                .findFirst()
                .orElse(null);
        if (game != null) {
            game.setRoundCreatedTime(System.currentTimeMillis());
            game.setStatus(Constant.GAME_STATUS_PLAYING);
        }
        return game;
    }

    public Game createGame(User user1, User user2) {
        logger.info("createGame: " + user1 + " " + user2);
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
            case 1 -> logger.info("Player " + game.getPlayer1().getUsername() + " goes first");
            case 2 -> logger.info("Player " + game.getPlayer2().getUsername() + " goes first");
        }
        game.setMoves(new ArrayList<>());
        return game;
    }

    public Game createGame(Player player1, Player player2, Integer whoFirst) {
        logger.info("createGame: " + player1 + " " + player2 + " " + whoFirst);
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
        logger.info("getGameById: " + id);
        return gameList.stream().filter(game -> game.getId().equals(id)).findFirst().orElse(null);
    }

    public void removeGameById(Long id) {
        logger.info("removeGameById: " + id);
        gameList.removeIf(game -> game.getId().equals(id));
    }

    public Game getGameByPlayerId(Long id) {
        logger.info("getGameByPlayerId: " + id);
        return gameList.stream().filter(game -> game.getPlayer1().getId().equals(id) || game.getPlayer2().getId().equals(id)).findFirst().orElse(null);
    }

    public void updateGameStatus(Long id, Integer status) {
        logger.info("updateGameStatus: " + id + " " + status);
        gameList.stream().filter(game -> game.getId().equals(id)).findFirst().ifPresent(game -> game.setStatus(status));
    }

    public Integer checkTimeout(Game game, Move move) {
        logger.info("checkTimeout (move): " + game + " " + move);
        int gameStatus = Constant.GAME_STATUS_PLAYING;
        // check timeout caused end
        if (game.getTurn() > 2) {
            Move opponentMove = game.getMoves().get(game.getMoves().size() - 2);
            if (move.getMoveTime() - opponentMove.getMoveTime() > Constant.GAME_TIMEOUT_MS + Constant.GAME_TIMEOUT_EPSILON_MS) {
                // player timeout, opponent win
                gameStatus = Objects.equals(game.getPlayer1(), move.getPlayer()) ? Constant.GAME_STATUS_PLAYER2_WIN : Constant.GAME_STATUS_PLAYER1_WIN;
                logger.info("Player " + move.getPlayer().getUsername() + " timeout");
            }
        } else {
            // use round created time to check timeout
            if (move.getMoveTime() - game.getRoundCreatedTime() > Constant.GAME_TIMEOUT_MS + Constant.GAME_TIMEOUT_EPSILON_MS) {
                gameStatus = Objects.equals(game.getPlayer1(), move.getPlayer()) ? Constant.GAME_STATUS_PLAYER2_WIN : Constant.GAME_STATUS_PLAYER1_WIN;
                logger.info("Player " + move.getPlayer().getUsername() + " timeout");
            }
        }
        return gameStatus;
    }

    public Integer checkTimeout(Game game, Long checkTime, Player player) {
        logger.info("checkTimeout (checkTime): " + game + " " + checkTime);
        int gameStatus = Constant.GAME_STATUS_PLAYING;
        // check timeout caused end
        if (game.getTurn() > 2) {
            Move opponentMove = game.getMoves().get(game.getMoves().size() - 2);
            if (checkTime - opponentMove.getMoveTime() > Constant.GAME_TIMEOUT_MS + Constant.GAME_TIMEOUT_EPSILON_MS) {
                // player timeout, opponent win
                gameStatus = Objects.equals(game.getPlayer1(), player) ? Constant.GAME_STATUS_PLAYER2_WIN : Constant.GAME_STATUS_PLAYER1_WIN;
                logger.info("Player " + player.getUsername() + " timeout");
            }
        } else {
            // use round created time to check timeout
            if (checkTime - game.getRoundCreatedTime() > Constant.GAME_TIMEOUT_MS + Constant.GAME_TIMEOUT_EPSILON_MS) {
                gameStatus = Objects.equals(game.getPlayer1(), player) ? Constant.GAME_STATUS_PLAYER2_WIN : Constant.GAME_STATUS_PLAYER1_WIN;
                logger.info("Player " + player.getUsername() + " timeout");
            }
        }
        return gameStatus;
    }

    public void updateGameMove(Game game, Move move) {
        logger.info("updateGameMove: " + game + " " + move);
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
        logger.info("(turn=" + game.getTurn() + ") " + "Player " + move.getPlayer().getUsername() + " move to " + move.getX() + ", " + move.getY());
        // check if game end

        Integer gameStatus = checkGameStatus(game, move);

        game.setTurn(game.getTurn() + 1);

        updateScores(game, gameStatus);
    }

    public void updateScores(Game game, Integer gameStatus) {
        if (gameStatus != Constant.GAME_STATUS_PLAYING) {
            game.setStatus(gameStatus);
            System.out.print("Game end: ");
            switch (gameStatus) {
                case Constant.GAME_STATUS_PLAYER1_WIN -> {
                    game.getPlayer1().setScore(game.getPlayer1().getScore() + 1);
                    logger.info("Player 1 win (" + game.getPlayer1().getUsername() + ")");
                }
                case Constant.GAME_STATUS_PLAYER2_WIN -> {
                    game.getPlayer2().setScore(game.getPlayer2().getScore() + 1);
                    logger.info("Player 2 win (" + game.getPlayer2().getUsername() + ")");
                }
                case Constant.GAME_STATUS_IT_IS_A_TIE -> {
                    game.getPlayer1().setScore(game.getPlayer1().getScore() + 1);
                    game.getPlayer2().setScore(game.getPlayer2().getScore() + 1);
                    logger.info("It is a tie");
                }
            }
        }
    }

    public Integer checkGameStatus(Game game, Move lastMove) {
        logger.info("checkGameStatus: " + game);
        Integer gameStatus = checkTimeout(game, lastMove);
        if (gameStatus != Constant.GAME_STATUS_PLAYING) {
            return gameStatus;
        }
        Integer[][] board = game.getBoard();
        Integer turn = game.getTurn();
        Integer whoFirst = game.getWhoFirst();
        Integer playerNumber = game.getNowTurnPlayerNumber();
        Integer playerStone = game.getPlayerStoneType(game.getPlayer(playerNumber));
        int row = lastMove.getX();
        int col = lastMove.getY();
        int minRow = Math.max(0, row - 4);
        int maxRow = Math.min(Constant.BOARD_SIZE - 1, row + 4);
        int minCol = Math.max(0, col - 4);
        int maxCol = Math.min(Constant.BOARD_SIZE - 1, col + 4);
        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minCol; j <= maxCol; j++) {
                if (Objects.equals(board[i][j], playerStone)) {
                    if (i + 4 <= maxRow && board[i + 1][j] == playerStone && board[i + 2][j] == playerStone && board[i + 3][j] == playerStone && board[i + 4][j] == playerStone) {
                        return playerNumber + Constant.GAME_STATUS_IT_IS_A_TIE;
                    }
                    if (j + 4 <= maxCol && board[i][j + 1] == playerStone && board[i][j + 2] == playerStone && board[i][j + 3] == playerStone && board[i][j + 4] == playerStone) {
                        return playerNumber + Constant.GAME_STATUS_IT_IS_A_TIE;
                    }
                    if (i + 4 <= maxRow && j + 4 <= maxCol && board[i + 1][j + 1] == playerStone && board[i + 2][j + 2] == playerStone && board[i + 3][j + 3] == playerStone && board[i + 4][j + 4] == playerStone) {
                        return playerNumber + Constant.GAME_STATUS_IT_IS_A_TIE;
                    }
                    if (i - 4 >= minRow && j + 4 <= maxCol && board[i - 1][j + 1] == playerStone && board[i - 2][j + 2] == playerStone && board[i - 3][j + 3] == playerStone && board[i - 4][j + 4] == playerStone) {
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
        logger.info("exitGame: " + game);
        game.setStatus(Constant.GAME_EXIT);
    }

}
