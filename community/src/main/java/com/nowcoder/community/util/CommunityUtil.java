package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @author 勋谦
 * @ClassName: CommunityUtil
 * @date: 2025/2/28 12:55
 * @Description:
 */
public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    // MD5加密
    // hello -> abc123def456
    // hello + 3e4a8 ->     // hello + 3e4a8 -> abc123def456abc
    public static String md5(String key){
        if(StringUtils.isBlank(key)){ // 判断字符串是否为空 null是空的，空格是空的，空串也是空的
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
