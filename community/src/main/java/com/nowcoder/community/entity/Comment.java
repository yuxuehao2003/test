package com.nowcoder.community.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author 勋谦
 * @ClassName: Comment
 * @date: 2025/3/3 18:44
 * @Description:
 */
@Getter
@Setter
@ToString
public class Comment {

    private int id;
    private int userId;
    private int entityType;
    private int entityId;
    private int targetId;
    private String content;
    private int status;
    private Date createTime;
}
