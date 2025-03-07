package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.CommuntiyConstant;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author 勋谦
 * @ClassName: FollowController
 * @date: 2025/3/7 15:22
 * @Description:
 */
@Controller
public class FollowController implements CommuntiyConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping("/follow")
    @ResponseBody
    public String follow(int entityType,int entityId){
        followService.follow(hostHolder.getUser().getId(),entityType,entityId);
        return CommunityUtil.getJSONString(0,"已关注");
    }

    @RequestMapping("/unfollow")
    @ResponseBody
    public String unFollow(int entityType,int entityId){
        followService.unFollow(hostHolder.getUser().getId(),entityType,entityId);
        return CommunityUtil.getJSONString(0,"已经取消关注");
    }

    @RequestMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403,"你还没有登录");
        }
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId,ENTITY_TYPE_USER));

        List<Map<String, Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());

        if(userList != null){
            userList.stream().forEach(map -> {
                User u = (User) map.get("user");
                // 重新检查是否关注
                map.put("hasFollowed",hasFollowed(u.getId()));
            });
        }
        model.addAttribute("users",userList);

        return "/site/followee";
    }

    private boolean hasFollowed(int userId){
        if(hostHolder.getUser() == null){
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(),CommuntiyConstant.ENTITY_TYPE_USER,userId);
    }

    @RequestMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403,"你还没有登录");
        }
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER,userId));

        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());

        if(userList != null){
            userList.stream().forEach(map -> {
                User u = (User) map.get("user");
                // 重新检查是否关注
                map.put("hasFollowed",hasFollowed(u.getId()));
            });
        }
        model.addAttribute("users",userList);

        return "/site/follower";
    }

}
