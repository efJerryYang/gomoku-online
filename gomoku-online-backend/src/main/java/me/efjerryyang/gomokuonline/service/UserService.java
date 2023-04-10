package me.efjerryyang.gomokuonline.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private List<String> waitingList;

    public List<String> getWaitingList() {
        return null;
    }

    public boolean pickUsername(String username) {
        return true;
    }
}
