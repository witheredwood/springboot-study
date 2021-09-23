package com.withered.mapper;

import com.withered.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper  // 表明这是一个mabatis 的 mapper 类
public interface UserMapper {
    List<User> get();

    User getUserById(int id);
}
