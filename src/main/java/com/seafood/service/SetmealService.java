package com.seafood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seafood.dto.SetmealDto;
import com.seafood.entity.Setmeal;
import com.seafood.entity.SetmealDish;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface SetmealService extends IService<Setmeal> {

    /**
     * 保存套餐表 (關聯套餐_菜品表)
     * @param setmealDto
     */
    void saveWithDish (SetmealDto setmealDto);

    /**
     * 刪除套餐 (多表)
     * @param ids
     */
    void removeWithDish(List<Long> ids);

    /**
     * 啟售 & 停售套餐
     * @param status
     * @param ids
     */
    void updateStatus (Integer status , List<Long> ids);

    /**
     * 修改回寫
     * @param id
     * @return
     */
    SetmealDto getByIdWithSetmealDish (Long id);

    /**
     * 修改套餐(多表操作)
     * @param setmealDto
     */
    void updateWithSetmealDish (SetmealDto setmealDto);

    /**
     * 套餐詳情展示
     * @param id
     * @return
     */
    List<SetmealDish> setmealDetails (Long id);
}
