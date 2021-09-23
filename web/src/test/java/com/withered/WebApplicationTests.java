package com.withered;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WebApplicationTests {
    @Autowired
    private Person person;

    @Autowired
    DataSource dataSource;

    @Test
    public void testPerson() {
        System.out.println(person);
    }

    @Test
    public void testConnection() throws SQLException {
        // 查看默认的数据源
        System.out.println(dataSource.getClass());  // class com.zaxxer.hikari.HikariDataSource

        Connection connection = dataSource.getConnection();  // 获取数据库连接
        System.out.println(connection);

        connection.close();  // 关闭连接
    }
    @Test
    void contextLoads() {
    }

}
