package me.efjerryyang.gomokuonline.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String username;
    private Long createTime;
    private Long id;
    private String clientId;
    private Integer status;
}
