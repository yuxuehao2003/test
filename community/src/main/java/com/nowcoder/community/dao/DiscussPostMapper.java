package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 勋谦
 * @interfaceName: DiscussPostMapper
 * @date: 2025/2/26 13:41
 * @Description:
 */
@Mapper

public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在<if>里使用,则必须加别名.
    int selectDiscussPostRows(@Param("userId") int userId);
}
