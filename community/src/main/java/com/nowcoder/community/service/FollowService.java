package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommuntiyConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 勋谦
 * @ClassName: FollowService
 * @date: 2025/3/7 15:17
 * @Description:
 */
@Service
public class FollowService implements CommuntiyConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;
    public void follow(int userId,int entityType,int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                redisOperations.multi();

                redisOperations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());
                redisOperations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());

                return redisOperations.exec();
            }
        });
    }

    public void unFollow(int userId,int entityType,int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                redisOperations.multi();

                redisOperations.opsForZSet().remove(followerKey,userId);
                redisOperations.opsForZSet().remove(followeeKey,entityId);

                return redisOperations.exec();
            }
        });
    }

    // 查询关注的实体的数量 查询我关注了多少人
    public long findFolloweeCount(int userId,int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        // 统计数量
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    // 查询实体的粉丝的数量 查询我有多少粉丝
    public long findFollowerCount(int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        // 统计数量
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    // 查询当前用户是否关注了该实体
    public boolean hasFollowed(int userId,int entityType,int entityId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().score(followeeKey,entityId) != null;
    }

    // 查询某用户关注的人
    public List<Map<String,Object>> findFollowees(int userId,int offset,int limit){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,ENTITY_TYPE_USER);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);

        if(targetIds == null){
            return null;
        }

        List<Map<String,Object>> list = new ArrayList<>();
        targetIds.stream().forEach(targetId -> {
            Map<String,Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        });
        return list;
    }

    // 查询某用户的粉丝
    public List<Map<String,Object>> findFollowers(int userId,int offset,int limit){
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER,userId);
        // 默认set是无序的，但是redis封装为有序的
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);

        if(targetIds == null){
            return null;
        }

        List<Map<String,Object>> list = new ArrayList<>();
        targetIds.stream().forEach(targetId -> {
            Map<String,Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        });
        return list;
    }


}
