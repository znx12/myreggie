package com.example.reggie_take_out.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.common.aop.LogAnnotation;
import com.example.reggie_take_out.entity.Dish;
import com.example.reggie_take_out.entity.SetmealDish;
import com.example.reggie_take_out.param.dto.DishDTO;
import com.example.reggie_take_out.service.DishService;
import com.example.reggie_take_out.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜品管理 前端控制器
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private DishService dishService;


    @LogAnnotation(module = "菜品操作",operation = "分页查询菜品")
    @GetMapping("/page")
    public R page(@RequestParam(value = "page", defaultValue = "1") Integer page,
                  @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                  @RequestParam(value = "name", required = false) String name){
        Page dishDTOList=dishService.page(page,pageSize,name);
        return R.success(dishDTOList);
    }


    @LogAnnotation(module = "菜品操作", operation = "查询菜品列表")
    @GetMapping("/list")
    public R page(Dish dish,@RequestParam(value = "name",required = false) String name) {
        List<DishDTO> dishDTOList = dishService.list(dish,name);
        return R.success(dishDTOList);
    }

    /**
     * 根据ID查询菜品信息以及对应的口味信息
     *
     * @param id
     * @return
     */
    @LogAnnotation(module = "菜品操作", operation = "单个查询菜品")
    @GetMapping("/{id}")
    public R<DishDTO> get(@PathVariable Long id) {
        DishDTO dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @LogAnnotation(module = "菜品操作",operation = "修改菜品")
    @PutMapping
    public R<String> update(@RequestBody DishDTO dishDTO){
        dishService.updateWithFlavor(dishDTO);
        return R.success("菜品修改成功");
    }

    @LogAnnotation(module = "菜品操作",operation = "增加菜品")
    @PostMapping
    public R save(@RequestBody DishDTO dishDTO){
        dishService.saveWithFlover(dishDTO);
        return R.success("菜品添加成功");
    }

    /**
     * 菜品的起售与停售
     *
     * @param ids
     * @return
     */
    @LogAnnotation(module = "菜品操作", operation = "菜品的起售与停售")
    @PostMapping("/status/{status}")
    public R<String> onOrClose(@PathVariable Integer status, Long[] ids) {

        for (int i = 0; i < ids.length; i++) {
            // 获取菜品
            Dish dish = dishService.getById(ids[i]);
            dish.setStatus(status);
            // 修改状态
            dishService.updateById(dish);
        }
        return R.success("修改成功");
    }

    /**
     * 菜品的删除
     * @param ids
     * @return
     */
    /**
     * 根据ID删除套餐信息，同时删除所关联的菜品
     *
     * @param ids
     * @return
     */
    @LogAnnotation(module = "菜品操作", operation = "根据ID删除套餐信息，同时删除所关联的菜品")
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {

        dishService.removeWithDish(ids);
        return R.success("菜品删除成功");
    }

    /**
     * 移动端点击套餐图片查看套餐具体内容
     * 这里返回的是dto 对象，因为前端需要copies这个属性
     * 前端主要要展示的信息是:套餐中菜品的基本信息，图片，菜品描述，以及菜品的份数
     *
     * @param SetmealId
     * @return
     */
    //这里前端是使用路径来传值的，要注意，不然你前端的请求都接收不到，就有点尴尬哈
    @GetMapping("/dish/{id}")
    public R<List<DishDTO>> dish(@PathVariable("id") Long SetmealId) {
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, SetmealId);
        //获取套餐里面的所有菜品  这个就是SetmealDish表里面的数据
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        List<DishDTO> dishDtos = list.stream().map((setmealDish) -> {
            DishDTO dishDto = new DishDTO();
            //其实这个BeanUtils的拷贝是浅拷贝，这里要注意一下
            BeanUtils.copyProperties(setmealDish, dishDto);
            //这里是为了把套餐中的菜品的基本信息填充到dto中，比如菜品描述，菜品图片等菜品的基本信息
            String dishId = setmealDish.getDishId();
            Dish dish = dishService.getById(dishId);
            BeanUtils.copyProperties(dish, dishDto);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtos);
    }

}

