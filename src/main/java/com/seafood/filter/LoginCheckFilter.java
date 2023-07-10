package com.seafood.filter;

import com.alibaba.fastjson.JSON;
import com.seafood.common.BaseContext;
import com.seafood.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登入攔截器
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //spring提供的路徑匹配器, 用來讓過濾器能夠識別通配符
    private  static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //獲取請求的URI
        String requestURI = request.getRequestURI();
        //log.info("攔截到了 {}",request.getRequestURI());

        //設定一定放行的路徑
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/login",
                "/user/sendMsg"
        };

        //判斷本次請求是否放行
        if (check(urls,requestURI)) {
            //放行
            filterChain.doFilter(request,response);
            return;
        }

        //判斷員工是否登入, 未登入直接跳轉回登入頁面
        Long empId = (Long) request.getSession().getAttribute("employee");

        if (empId != null){

                long threadId = Thread.currentThread().getId();
                log.info("當前執行緒ID= {}",threadId);

                BaseContext.setCurrentId(empId);

                filterChain.doFilter(request,response);
                return;
            }

        //判斷用戶是否登入, 未登入直接跳轉回登入頁面
        Long userId = (Long) request.getSession().getAttribute("user");

        if (userId != null){

            long threadId = Thread.currentThread().getId();
            log.info("當前執行緒ID= {}",threadId);

            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }

        //登入失敗用responseWriter響應JSON的result數據
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));

        return;
    }

    /**
     * 判斷是否放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check (String[] urls, String requestURI){

        for (String url : urls) {
            //判斷當前請求URI是否和要放行的路徑吻合
            boolean match = ANT_PATH_MATCHER.match(url, requestURI);

            if(match){
                return true;
            }

        }

        return false;
    }
}
