package com.nowcoder.community.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author 勋谦
 * @ClassName: Message
 * @date: 2025/3/4 14:31
 * @Description:
 */
@Getter
@Setter
@ToString
public class Message {

    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    private int status;
    private Date createTime;


}
