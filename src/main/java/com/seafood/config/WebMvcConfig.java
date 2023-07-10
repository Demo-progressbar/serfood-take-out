package com.seafood.config;

import com.seafood.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 設置靜態資源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("靜態資源映射加載........");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");

    }

    /**
     * 擴展MVC框架的消息轉換器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {


        log.info("消息轉換器加載~~~~");
        //創建消息轉換器物件
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //設置消息轉換器物件, 底層是使用Jackson將Java物件轉換為JSON
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //將上面設置好的轉換器物件+到MVC框架的轉換器集合裡, 參數1:轉換器的優先級排序 參數2:自己配置的轉換器
        converters.add(0,messageConverter);




    }
}
