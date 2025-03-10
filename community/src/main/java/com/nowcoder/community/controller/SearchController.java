package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommuntiyConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 勋谦
 * @ClassName: SearchController
 * @date: 2025/3/10 19:24
 * @Description:
 */
@Controller
public class SearchController implements CommuntiyConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping("/search")
    public String search(String keyword, Page page, Model model){
        // 搜索帖子

        org.springframework.data.domain.Page<DiscussPost> searchResult = elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());

        // 聚合数据
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(searchResult != null){
            searchResult.forEach(post -> {
                Map<String, Object> map = new HashMap<>();

                map.put("post",post);
                map.put("user",userService.findUserById((Integer) post.getUserId()));
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,(Integer) post.getId()));

                discussPosts.add(map);
            });
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("keyword",keyword);

        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());
        page.setPath("/search?keyword=" + keyword);
        return "/site/search";

    }
}
