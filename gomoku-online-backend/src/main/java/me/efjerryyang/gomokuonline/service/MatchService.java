package me.efjerryyang.gomokuonline.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import me.efjerryyang.gomokuonline.dto.MatchDTO;
@Service
public class MatchService {
    @Autowired
    private UserService userService;

//    public List<Integer> getWaitingList() {
//        return userService.getWaitingList();
//    }
}
