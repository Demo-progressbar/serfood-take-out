package com.seafood.dto;


import com.seafood.entity.Setmeal;
import com.seafood.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
