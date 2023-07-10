package com.seafood.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seafood.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分類Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
