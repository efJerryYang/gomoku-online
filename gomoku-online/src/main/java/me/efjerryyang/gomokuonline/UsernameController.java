package me.efjerryyang.gomokuonline;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsernameController {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        System.out.println("Username set to " + username);
        this.username = username;
    }

    @PostMapping("/api/pick")
    public String pickUsername(String username) {
        System.out.println("Username picked: " + username);
        setUsername(username);
        return "redirect:/";
    }
}
