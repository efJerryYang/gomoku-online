package me.efjerryyang.gomokuonline;


public class Constant {
    // User status
    public static final int USER_STATUS_WAITING = 0;
    public static final int USER_STATUS_MATCHED = 1;
    public static final int USER_STATUS_PLAYING = 2;
    public static final int USER_STATUS_OFFLINE = 3;

    // Time seconds
    public static final long MINUTE_TO_SECONDS = 60;
    public static final long HOUR_TO_SECONDS = 60 * MINUTE_TO_SECONDS;
    public static final long DAY_TO_SECONDS = 24 * HOUR_TO_SECONDS;
    public static final long WEEK_TO_SECONDS = 7 * DAY_TO_SECONDS;

    // Board info
    public static final int BOARD_SIZE = 10;
    public static final int BACKGROUND_CELL = 0;
    public static final int WHITE_CELL = 1;
    public static final int BLACK_CELL = 2;

    // Game info
    public static final int GAME_STATUS_PENDING = 0;
    public static final int GAME_STATUS_PLAYING = 1;
    public static final int GAME_STATUS_IT_IS_A_TIE = 2;
    public static final int GAME_STATUS_PLAYER1_WIN = 3;
    public static final int GAME_STATUS_PLAYER2_WIN = 4;
}
