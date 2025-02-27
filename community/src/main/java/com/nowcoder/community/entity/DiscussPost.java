package com.nowcoder.community.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author 勋谦
 * @ClassName: DiscussPost
 * @date: 2025/2/26 13:36
 * @Description:
 */

@Data
@Getter
@Setter
@ToString
public class DiscussPost {

    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;

}
