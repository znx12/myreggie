package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie_take_out.common.BaseContext;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.entity.ShoppingCart;
import com.example.reggie_take_out.mapper.ShoppingCartMapper;
import com.example.reggie_take_out.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 购物车 服务实现类
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {
        //设置用户id 指定是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        /**
         *构造查询条件
         * 1.用户id 2.是否是菜品id 3.套餐
         **/
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        Long dishId = shoppingCart.getDishId();
        //查询菜品是否在购物车里面
        if(dishId!=null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart shoppingCartOne = this.getOne(queryWrapper);
        //如果已经存在 则数量加一
        if(shoppingCartOne!=null){
            Integer number = shoppingCartOne.getNumber();
            shoppingCartOne.setNumber(number+1);
            this.updateById(shoppingCartOne);
        }
        //不存在 则添加至购物车 设置数量为1
        else {
            shoppingCart.setNumber(1);
            this.save(shoppingCart);
            shoppingCartOne=shoppingCart;
        }
        return shoppingCartOne;
    }

    @Override
    public List<ShoppingCart> getByUserId() {
        /**
         * 构建查询条件 用户id 时间降序排序
         */
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        return this.list(queryWrapper);
    }

    @Transactional
    @Override
    public R sub(ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        //用户id
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        //菜品数量的减少
        if(dishId!=null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            ShoppingCart cartOne = this.getOne(queryWrapper);
            cartOne.setNumber(cartOne.getNumber()-1);
            Integer number = cartOne.getNumber();
            //进行更新操作
            if(number>0){
                this.updateById(cartOne);
            }else if(number==0){
                this.removeById(cartOne);
            }else if(number<0){
                return R.error("操作错误");
            }
            return R.success(cartOne);
        }
        Long setmealId = shoppingCart.getSetmealId();
        if(setmealId!=null){
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
            ShoppingCart cart2 = this.getOne(queryWrapper);
            cart2.setNumber(cart2.getNumber()-1);
            Integer cart2Number = cart2.getNumber();
            if(cart2Number>0){
                this.updateById(cart2);
            }else if(cart2Number==0){
                this.removeById(cart2);
            }else if(cart2Number<0){
                return R.error("操作错误");
            }
            return R.success(cart2);
        }
        return R.error("操作异常");
    }
}
