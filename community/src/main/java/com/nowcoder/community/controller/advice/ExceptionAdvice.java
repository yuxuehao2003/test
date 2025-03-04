package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import lombok.extern.apachecommons.CommonsLog;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author 勋谦
 * @ClassName: ExceptionAdvice
 * @date: 2025/3/4 16:51
 * @Description:
 */
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request,HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常："+e.getMessage());
        for(StackTraceElement element : e.getStackTrace()){
            logger.error(element.toString());
        }

        String xRequestWith = request.getHeader("x-requested-with");
        // 表示其是一个异步请求
        if("XMLHttpRequest".equals(xRequestWith)){
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"服务器异常！"));
        } else {
            response.sendRedirect(request.getContextPath()+"/error");
        }
    }
}
