package com.example.reggie_take_out.param.dto;

import com.example.reggie_take_out.entity.Setmeal;
import com.example.reggie_take_out.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetMealDTO extends Setmeal {
    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
