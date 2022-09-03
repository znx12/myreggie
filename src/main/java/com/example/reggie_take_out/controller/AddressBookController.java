package com.example.reggie_take_out.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.reggie_take_out.common.BaseContext;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.common.aop.LogAnnotation;
import com.example.reggie_take_out.entity.AddressBook;
import com.example.reggie_take_out.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 地址管理 前端控制器
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 保存地址
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        AddressBook lastAddress=addressBookService.setDefault(addressBook);
        return R.success(lastAddress);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id){
        return R.success(addressBookService.getById(id));
    }

    /**
     * 查询默认地址
     */
    @LogAnnotation(module = "查看默认地址",operation = "sd")
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        AddressBook addressBook = addressBookService.getOne(new LambdaQueryWrapper<AddressBook>().eq(AddressBook::getIsDefault, 1).eq(AddressBook::getUserId, BaseContext.getCurrentId()));
        log.info(addressBook.toString());
        return R.success(addressBook);
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(){
        List<AddressBook> addressBookList = addressBookService.list(new LambdaQueryWrapper<AddressBook>().eq(AddressBook::getUserId, BaseContext.getCurrentId()).orderByDesc(AddressBook::getUpdateTime));
        return R.success(addressBookList);
    }

    /**
     * 根据地址id删除 用户地址
     */
    @DeleteMapping
    public R delete(@RequestParam("ids") Long id){
        boolean remove = addressBookService.remove(new LambdaQueryWrapper<AddressBook>().eq(AddressBook::getUserId, BaseContext.getCurrentId()).eq(AddressBook::getId, id));
        return R.success(remove);
    }

    /**
     * 修改收货地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook) {
        if (addressBook == null) {
            return R.error("请求异常");
        }
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }







}

