package me.efjerryyang.gomokuonline.service;

import me.efjerryyang.gomokuonline.Constant;
import me.efjerryyang.gomokuonline.dto.MatchGetDTO;
import me.efjerryyang.gomokuonline.entity.Game;
import me.efjerryyang.gomokuonline.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final List<User> userList;
    private final List<MatchGetDTO> waitingList;
    @Autowired
    private GameService gameService;

    public UserService() {
        userList = new ArrayList<>();
        waitingList = new ArrayList<>();
        initUserList();
        initWaitingList();
    }

    private void initUserList() {
        // Do nothing because no database
    }

    private void initTestUserList() {
        List<String> usernameList = List.of("testUser1", "testUser2", "testUser3", "testUser4", "testUser5");
        for (String username : usernameList) {
            if (pickUsername(username, username) != null) {
                System.out.println("User " + username + " added to waiting list");
            }
        }
    }

    private void initWaitingList() {
        waitingList.clear();
        for (User user : userList) {
            updateWaitingList(user);
        }
    }

    public User getUserByClientId(String clientId) {
        return userList.stream().filter(user -> user.getClientId().equals(clientId)).findFirst().orElse(null);
    }

    public User getUserById(Long id) {
        return userList.stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }

    public List<MatchGetDTO> getWaitingList() {
        return waitingList;
    }

    public void updateWaitingList(User user) {
        // if user not in waiting list and status is waiting, add to waiting list
        if (waitingList.stream().noneMatch(matchGetDTO -> matchGetDTO.getId().equals(user.getId())) && user.getStatus() == Constant.USER_STATUS_WAITING) {
            MatchGetDTO matchGetDTO = new MatchGetDTO();
            matchGetDTO.setUsername(user.getUsername());
            matchGetDTO.setId(user.getId());
            matchGetDTO.setJoinTime(System.currentTimeMillis() / 1000);
            waitingList.add(matchGetDTO);
        }
    }

    public void removeClient(String clientId) {
        // First get the userId with specific clientID, then remove the user with this userId from both userlist and waitinglist
        Long userId = userList.stream().filter(user -> user.getClientId().equals(clientId)).map(User::getId).findFirst().orElse(null);
        if (userId != null) {
            System.out.println("Removing client " + clientId + " with userId " + userId);
            userList.removeIf(user -> user.getId().equals(userId));
            waitingList.removeIf(matchGetDTO -> matchGetDTO.getId().equals(userId));
        }
    }

    public User pickUsername(String username, String clientId) {
        User newUser = new User();
        // Use generated uuid as id
        newUser.setId((long) (Math.random() * 1_0000_0000));
        newUser.setUsername(username);
        newUser.setStatus(Constant.USER_STATUS_WAITING);
        newUser.setCreateTime((System.currentTimeMillis() / 1000));
        newUser.setClientId(clientId);
        removeClient(clientId);
        userList.add(newUser);
        System.out.println("User " + username + " (" + newUser.getId() + ")" + " added to user list with clientId " + clientId);
        return newUser;
    }

    public Game matchPlayers(Long playerId1, Long playerId2) {
        if (playerId1 == null || playerId2 == null || playerId1.equals(playerId2)) {
            return null;
        }
        // remove from waiting list
        waitingList.removeIf(matchGetDTO -> matchGetDTO.getId().equals(playerId1));
        waitingList.removeIf(matchGetDTO -> matchGetDTO.getId().equals(playerId2));
        // change status to playing
        userList.stream().filter(user -> user.getId().equals(playerId1)).findFirst().ifPresent(user -> user.setStatus(Constant.USER_STATUS_PLAYING));
        userList.stream().filter(user -> user.getId().equals(playerId2)).findFirst().ifPresent(user -> user.setStatus(Constant.USER_STATUS_PLAYING));
        User user1 = getUserById(playerId1);
        User user2 = getUserById(playerId2);
        return gameService.createGame(user1, user2);
    }

    public synchronized Game matchWithRandomPlayer(Long playerId) {
        if (playerId == null) {
            return null;
        }
        if (waitingList.size() < 2) {
            return null;
        }

        waitingList.removeIf(matchGetDTO -> matchGetDTO.getId().equals(playerId));
        int index = (int) (waitingList.size() * Math.random()); // [0.0, 1.0)
        Long opponentId = waitingList.get(index).getId();

        // Check if the opponent is the same as the player
        if (opponentId.equals(playerId)) {
            for (int i = 0; i < waitingList.size(); i++) {
                if (!opponentId.equals(waitingList.get(i).getId())) {
                    index = i;
                    break;
                }
            }
        }
        waitingList.remove(index);

        userList.stream().filter(user -> user.getId().equals(playerId)).findFirst().ifPresent(user -> user.setStatus(Constant.USER_STATUS_PLAYING));
        userList.stream().filter(user -> user.getId().equals(opponentId)).findFirst().ifPresent(user -> user.setStatus(Constant.USER_STATUS_PLAYING));
        User user1 = getUserById(playerId);
        User user2 = getUserById(opponentId);
        return gameService.createGame(user1, user2);
    }

}
