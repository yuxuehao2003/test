package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommuntiyConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * @author 勋谦
 * @ClassName: HomeController
 * @date: 2025/2/26 20:59
 * @Description:
 */
@Controller
@Slf4j
public class HomeController implements CommuntiyConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping("/index")
    public String getIndexPage(Model model, Page page){
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> discussPosts = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPostsMap = new ArrayList<>();
        if (discussPosts!= null) {
            for (DiscussPost discussPost : discussPosts){
                Map<String,Object> map = new HashMap<>();
                map.put("post",discussPost);
                User user = userService.findUserById(discussPost.getUserId());
                map.put("user",user);

                // 查看帖子的点赞数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
                map.put("likeCount",likeCount);

                discussPostsMap.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPostsMap);

        return "/index";
    }

    @RequestMapping("/error1")
    public String getErrorPage(){
        return "/error/500";
    }

    // 拒绝访问时的提示页面
    @RequestMapping("/denied")
    public String getDeniedPage(){
        return "/site/404";
    }



}
