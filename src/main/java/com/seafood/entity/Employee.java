package com.seafood.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 員工實體
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber; //身分證字號

    private Integer status;

    @TableField(fill = FieldFill.INSERT) //MP提供自動設置公共字段註解, 新增時填入字段
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE) //新增和更新時填入字段
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
