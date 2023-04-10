package me.efjerryyang.gomokuonline.service;

import me.efjerryyang.gomokuonline.Constant;
import me.efjerryyang.gomokuonline.dto.MatchGetDTO;
import me.efjerryyang.gomokuonline.entity.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final List<User> userList;
    private final List<MatchGetDTO> waitingList;

    public UserService() {
        userList = new ArrayList<>();
        initUserList();
        waitingList = new ArrayList<>();
        initWaitingList();
    }

    private void initUserList() {
        List<String> usernameList = List.of("testUser1", "testUser2", "testUser3", "testUser4", "testUser5");
        for (String username : usernameList) {
            if (pickUsername(username)) {
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

    public boolean pickUsername(String username) {
        User newUser = new User();
        // Use generated uuid as id
        newUser.setId((long) (Math.random() * 1_0000_0000));
        newUser.setUsername(username);
        newUser.setStatus(Constant.USER_STATUS_WAITING);
        newUser.setCreateTime((System.currentTimeMillis() / 1000));
        userList.add(newUser);
        return true;
    }
}
