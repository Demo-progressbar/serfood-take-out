package com.seafood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seafood.dto.DishDto;
import com.seafood.entity.Dish;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
public interface DishService extends IService<Dish> {
    /**
     * 保存菜品, 要查兩張表dish flavor
     * @param dishDto
     */
    public void saveWithFlavor (DishDto dishDto);

    /**
     * 根據Id查詢菜品, 需查兩張表dish flavor
     */
    public DishDto getByIdWithFlavor(Long id);

    /**
     * 修改菜品, 需改兩張表dish flavor
     * @param dishDto
     */
    public void updateWithFlavor(DishDto dishDto);

    /**
     * 刪除菜品, 需刪兩張表dish flavor
     * @param ids
     */
    void deleteWithFlavor (List<Long> ids);

    /**
     * 根據ids修改停售&啟售
     * @param status
     * @param ids
     */
    void updateStatusByIds (Integer status , List<Long> ids);
}
