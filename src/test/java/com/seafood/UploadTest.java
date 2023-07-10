package com.seafood;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class UploadTest {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 操作string類型數據
     */
    @Test
    public void testString() {

        ValueOperations valueOperations = redisTemplate.opsForValue();

        //正常設值
        valueOperations.set("city", "taiwan");

        //設定超時時間
        valueOperations.set("ben", "ben123", 10l, TimeUnit.SECONDS);

        //假如不存在, 則新增
        Boolean aBoolean = valueOperations.setIfAbsent("aaaa", "abc");

        System.out.println(aBoolean);


    }

    /**
     * 操作hash類型數據
     */
    @Test
    public void testHash() {

        //操作哈希數據類型
        HashOperations hashOperations = redisTemplate.opsForHash();

        //存值
        hashOperations.put("002", "name", "benson");
        hashOperations.put("002", "age", "20");
        hashOperations.put("002", "address", "HsinChu");

        //取值
        String name = (String) hashOperations.get("002", "name");

        System.out.println(name);
        
        //獲取所有HashKey
        Set keys = hashOperations.keys("002");

        for (Object key : keys) {

            System.out.println(key);
        }
        System.out.println("HashValues~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        //獲取所有value
        List values = hashOperations.values("002");

        for (Object value : values) {
            System.out.println(value);
        }


    }

    /**
     * 存取list數據
     */
    @Test
    public void listTest (){

        ListOperations listOperations = redisTemplate.opsForList();

        //存值
        listOperations.leftPush("mylist" , "a");
        listOperations.leftPushAll("mylist" ,"b" ,"c" ,"d");

        //取值
        List<String> mylist = listOperations.range("mylist", 0, 1);

        for (String s : mylist) {
            System.out.println(s);
        }

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        //獲得列表長度
        Long size = listOperations.size("mylist");

        int lsize = size.intValue();

        for (int i = 0; i < lsize; i++) {

            String mylist1 = (String) listOperations.rightPop("mylist");

            System.out.println(mylist1);
        }
        


    }

    /**
     * 操作set類型數據
     */
    @Test
    public  void  setTest (){

        SetOperations setOperations = redisTemplate.opsForSet();

        //存值
        setOperations.add("myset" , "a" ,"b" , "c" ,"a");

        //取值
        Set myset = setOperations.members("myset");

        for (Object o : myset) {

            System.out.println(o);

        }

        System.out.println("~~~~~");

        //刪除成員
         setOperations.remove("myset", "c", "b");

        myset = setOperations.members("myset");

        for (Object o : myset) {

            System.out.println(o);
        }


    }

    /**
     * 操作Zset數據類型
     */
    @Test
    public void testZset(){

        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

        //存值
        zSetOperations.add("myZset" , "a" , 10.0);
        zSetOperations.add("myZset" , "b" , 11.0);
        zSetOperations.add("myZset" , "c" , 12.0);
        zSetOperations.add("myZset" , "d" , 13.0);

        //取值
        Set myZset = zSetOperations.range("myZset", 0, -1);

        for (Object o : myZset) {
            System.out.println(o);
        }

        System.out.println("~~~~~");


        //修改分數

        zSetOperations.incrementScore("myZset" , "a" ,14.0);

         myZset = zSetOperations.range("myZset", 0, -1);

        for (Object o : myZset) {
            System.out.println(o);
        }

        System.out.println("~~~~~~~~~~~");

        //刪除成員

        zSetOperations.remove("myZset" ,"a" ,"b");

        myZset = zSetOperations.range("myZset", 0, -1);

        for (Object o : myZset) {
            System.out.println(o);
        }

    }

    /**
     * 通用操作
     */
    @Test
    public void common (){

        //獲取所有key
        Set keys = redisTemplate.keys("*");

        for (Object key : keys) {
            System.out.println(key);
        }


        //判斷某個key是否存在
        Boolean itcast = redisTemplate.hasKey("itcast");
        System.out.println(itcast);

        //刪除指定key
        Boolean myZset = redisTemplate.delete("myZset");


        //獲取指定key對應的value類型
        DataType myset = redisTemplate.type("myset");

        System.out.println(myset.name());



    }
}
