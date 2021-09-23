package com.withered.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class JdbcController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    // 不写实体类， 获取数据库中的数据
    // 查询数据库所有数据
    @GetMapping("/get")
    public List<Map<String, Object>> get() {
        String sql = "select * from myweb.user";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }

    @GetMapping("/add")
    public String add() {
        String sql = "insert into myweb.user(id, name, age, sex) values (6, '小明', 15, '男')";
        jdbcTemplate.update(sql);
        return "add ok";
    }

    // 只能修改数据库中已存在的数据
    @GetMapping("/update/{id}")
    public String update(@PathVariable("id") int id) {
        String sql = "update myweb.user set name=?, age=?, sex=? where id = " + id;
        // 封装对象。将要设置的数据封装成对象传入
        Object[] objects = new Object[3];  // 要传递的参数
        objects[0] = "小红";
        objects[1] = 5;
        objects[2] = "女";
        jdbcTemplate.update(sql, objects);
        return "update ok";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        // 第一种方式
        String sql = "delete from myweb.user where id = " + id;
        jdbcTemplate.update(sql);
        // 第二种方式
//        String sql = "delete from myweb.user where id = ?";
//        jdbcTemplate.update(sql, id);
        return "delete ok";
    }
}
