package me.efjerryyang.gomokuonline.service;

import me.efjerryyang.gomokuonline.entity.Game;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

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

}
