package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
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

    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map != null){
            for(String key : map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        return getJSONString(code,msg,null);
    }

    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }

    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("name","张三");
        map.put("age",25);
        System.out.println(getJSONString(0,"ok",map));
        // {"msg":"ok","code":0,"name":"张三","age":25}

    }
}
