package com.example.reggie_take_out.service;

import com.example.reggie_take_out.entity.AddressBook;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 地址管理 服务类
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
public interface AddressBookService extends IService<AddressBook> {

    AddressBook setDefault(AddressBook addressBook);
}
