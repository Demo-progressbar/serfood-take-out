package com.seafood.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seafood.common.BaseContext;
import com.seafood.common.Result;
import com.seafood.entity.User;
import com.seafood.service.UserService;
import com.seafood.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 發送驗證碼
     * @param user
     * @param session
     * @return
     */
    @PostMapping("sendMsg")
    public Result<String> sendMsg(@RequestBody User user , HttpSession session){

        log.info("發送獲取驗證碼請求參數 = {}",user.getPhone());

        //獲取手機號碼
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)) {

            //生成驗證碼
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("生成的豔碼 {}",code);

            //發送驗證碼信箱
            /*
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("q964821337@gmail.com");
            message.setTo(phone);
            message.setSubject("seafood登入驗證碼");
            message.setText("您的燈入驗證碼為: "+code);
            javaMailSender.send(message);
            */

            //把生成的驗證碼存入session
            //session.setAttribute(phone,code);

            //使用redis儲存登入驗證碼
            redisTemplate.opsForValue().set(phone ,code , 5l , TimeUnit.MINUTES);

            return Result.success("驗證碼發送成功");

        }

        return Result.error("驗證碼發送失敗");
    }

    /**
     * 信箱登入(移動端)
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Result<User> login (@RequestBody Map map ,HttpSession session){

        log.info("信箱登入參數 {}",map);

        //獲取前端傳送來的參數
        String phone = map.get("phone").toString();

        String code = map.get("code").toString();

        //從session獲取驗證碼
        //Object sessionInCode = session.getAttribute(phone);

        //從redis獲取驗證碼
        Object sessionInCode = redisTemplate.opsForValue().get(phone);

        //判斷用戶輸入的code是否正確
        if(sessionInCode != null && sessionInCode.equals(code)){

            //判斷該用戶是否為新用戶
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

            queryWrapper.eq(User::getPhone ,phone);

            User user = userService.getOne(queryWrapper);

            //如果用戶不存在, 註冊新用戶
            if (user == null){

                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            //將登入的用戶訊息存入session
            session.setAttribute("user" ,user.getId());

            //登入成功, 刪除redis緩存中的驗證碼數據
            redisTemplate.delete(phone);

            return Result.success(user);

        }



        return Result.error("登入失敗");
    }

    /**
     * 用戶登出
     * @param session
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout (HttpSession session){

        log.info("用戶登出");
        //獲取用戶Id
        Long currentId = BaseContext.getCurrentId();

        session.removeAttribute("user");



        return Result.success("用戶已登出");
    }

}
