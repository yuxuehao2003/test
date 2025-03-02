package com.nowcoder.community.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author 勋谦
 * @ClassName: LoginTicket
 * @date: 2025/2/28 20:48
 * @Description:
 */
@Getter
@Setter
@ToString
public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
}
