package com.nowcoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.CommuntiyConstant;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author 勋谦
 * @ClassName: MessageController
 * @date: 2025/3/4 15:17
 * @Description:
 */
@Controller
public class MessageController implements CommuntiyConstant {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping("/letter/list")
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        // 会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(),  page.getOffset(), page.getLimit());

        List<Map<String,Object>> conversations = new ArrayList<>();

        if(conversationList != null){
            conversationList.stream().forEach(message -> {
                Map<String,Object> map = new HashMap<>();
                map.put("conversation",message);
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target",userService.findUserById(targetId));
                conversations.add(map);
            });
        }

        model.addAttribute("conversations",conversations);

        // 查询未读私信数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        // 查询未读通知数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "/site/letter";
    }

    @RequestMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model){
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // 私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        if(letterList != null){
            letterList.stream().forEach(letter -> {
                Map<String,Object> map = new HashMap<>();
                map.put("letter",letter);
                map.put("fromUser",userService.findUserById(letter.getFromId()));
                letters.add(map);
            });
        }

        model.addAttribute("letters",letters);
        // 私信目标
        model.addAttribute("target",getLetterTarget(conversationId));

        // 设置为已读
        List<Integer> ids = getLetterIds(letterList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");

        int d0 = Integer.parseInt(ids[0]);
        int d1 = Integer.parseInt(ids[1]);

        if(hostHolder.getUser().getId() == d0){
            return userService.findUserById(d1);
        } else{
            return userService.findUserById(d0);
        }

    }

    // 已读
    private List<Integer> getLetterIds(List<Message> messages){
        List<Integer> ids = new ArrayList<>();
        if(messages!= null){
            messages.stream().forEach(message -> {
                if(hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            });
        }
        return ids;
    }

    // 发送信息
    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName,String content){

        User target = userService.findUserByName(toName);
        if(target == null){
            return CommunityUtil.getJSONString(1,"目标用户不存在!");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        // 会话ID
        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else{
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }

    @RequestMapping("/notice/list")
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();

        // 查询评论类通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);

        if(message != null){
            Map<String,Object> messageVo = new HashMap<>();
            // 传入message
            messageVo.put("message",message);

            // 传入content，将content转成map
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVo.put("user",userService.findUserById((Integer)data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("postId",data.get("postId"));

            // 传入通知数量
            int count = messageService.findNoticeCount(user.getId(),TOPIC_COMMENT);
            messageVo.put("count",count);

            // 传入未读数量
            int unread = messageService.findNoticeUnreadCount(user.getId(),TOPIC_COMMENT);
            messageVo.put("unread",unread);
            model.addAttribute("commentNotice",messageVo);
        }



        // 查询点赞类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);

        if(message != null){
            Map<String,Object> messageVo = new HashMap<>();
            // 传入message
            messageVo.put("message",message);

            // 传入content，将content转成map
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVo.put("user",userService.findUserById((Integer)data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("postId",data.get("postId"));

            // 传入通知数量
            int count = messageService.findNoticeCount(user.getId(),TOPIC_LIKE);
            messageVo.put("count",count);

            // 传入未读数量
            int unread = messageService.findNoticeUnreadCount(user.getId(),TOPIC_LIKE);
            messageVo.put("unread",unread);
            model.addAttribute("likeNotice",messageVo);
        }



        // 查询关注类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);

        if(message != null){
            Map<String,Object> messageVo = new HashMap<>();
            // 传入message
            messageVo.put("message",message);

            // 传入content，将content转成map
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVo.put("user",userService.findUserById((Integer)data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));

            // 传入通知数量
            int count = messageService.findNoticeCount(user.getId(),TOPIC_FOLLOW);
            messageVo.put("count",count);

            // 传入未读数量
            int unread = messageService.findNoticeUnreadCount(user.getId(),TOPIC_FOLLOW);
            messageVo.put("unread",unread);

            model.addAttribute("followNotice",messageVo);
        }



        // 查询未读私信数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        // 查询未读通知数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "/site/notice";
    }

    @RequestMapping("/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model){
        User user = hostHolder.getUser();
        // 设置分页信息
        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        List<Message> notices = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String,Object>> noticeVoList = new ArrayList<>();

        if(notices != null){
            notices.stream().forEach(notice -> {
                Map<String,Object> map = new HashMap<>();
                /// 通知
                map.put("notice",notice);
                // 内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user",userService.findUserById((Integer) data.get("userId")));
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));

                // 通知的作者
                map.put("fromUser",userService.findUserById(notice.getFromId()));

                noticeVoList.add(map);
            });
        }

        model.addAttribute("noticea",noticeVoList);

        // 设置已读
        List<Integer> ids = getLetterIds(notices);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";

    }
}
