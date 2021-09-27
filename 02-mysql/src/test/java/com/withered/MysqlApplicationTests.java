package com.withered;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class MysqlApplicationTests {
    @Autowired
    DataSource dataSource;

    @Test
    public void testConnection() throws SQLException {
        // 查看默认的数据源
        System.out.println(dataSource.getClass());  // class com.zaxxer.hikari.HikariDataSource

        Connection connection = dataSource.getConnection();  // 获取数据库连接
        System.out.println(connection);

        connection.close();  // 关闭连接
    }

}
