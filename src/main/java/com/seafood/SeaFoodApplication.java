package com.seafood;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j //log相關
@SpringBootApplication
@ServletComponentScan //掃描webServlet元件
@EnableTransactionManagement //開啟交易功能
public class SeaFoodApplication {

    public static void main(String[] args) {

        SpringApplication.run(SeaFoodApplication.class,args);

        log.info("項目啟動成功~~~");
    }
}
