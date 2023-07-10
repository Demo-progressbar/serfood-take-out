package com.seafood.controller;


import com.seafood.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${seafood.path}")
    private String basePath;

    /**
     * 文件上傳
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        //file是臨時文件, 需要把他轉存到其他地方, 否則當次請求結束後就會消失
        log.info("上傳文件為:"+file.toString());

        //原始文件名
        String originalFilename = file.getOriginalFilename();

        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID生成文件名, 防止文件名稱重複
        String fileName = UUID.randomUUID().toString() + suffix;

        //創建一個文件夾
        File dir = new File(basePath);

        //判斷該文件夾是否存在
        if(!dir.exists()){
            //文件夾不存在, 創建一個
            dir.mkdirs();
        }

      try {
            //將檔案轉存到指定路徑
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Result.success(fileName);
    }

    /**
     * 下載文件
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download (String name , HttpServletResponse response){


        try {
            //創建字節輸入流讀取圖片
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));

            //拿到響應的字節輸出流響應數據
            ServletOutputStream outputStream = response.getOutputStream();

            //設定響應的數據類型
            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];

            while ((len = fileInputStream.read(bytes)) !=-1) {
                //寫回數據
                outputStream.write(bytes ,0,len);
                //刷新緩存
                outputStream.flush();
            }
            //釋放資源
            outputStream.close();
            fileInputStream.close();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
