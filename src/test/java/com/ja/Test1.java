package com.ja;

import com.ja.dao.UserDao;
import com.ja.entity.User;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class Test1 {

    @Test
    public void testXml() throws IOException {
        String resource = "mybatis/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            for (int i = 0; i < 10; i++) {
                int sex = (int) (Math.random() * 2);
                User user = new User();
                user.setName("小明" + i);
                user.setSex(String.valueOf(sex));
                user.setAge((int) (Math.random() * 30));
                UserDao userDao = sqlSession.getMapper(UserDao.class);
                userDao.insert(user);
                Integer id = user.getId();
                System.out.println(id);
            }
        }
    }

    @Test
    public void testNotXml() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("demo.properties");
        properties.load(inputStream);
        String driver = properties.getProperty("jdbc.driver");
        String url = properties.getProperty("jdbc.url1");
        String username = properties.getProperty("jdbc.username");
        String passwrod = properties.getProperty("jdbc.password");
        DataSource dataSource = new PooledDataSource(driver, url, username, passwrod);
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("demo", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMappers("com.ja.dao");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            for (int i = 0; i < 10; i++) {
                User user = new User();
                user.setName("小明" + i);
                user.setSex(String.valueOf((int) (Math.random() * 2)));
                user.setAge((int) (Math.random() * 30));
                UserDao userDao = sqlSession.getMapper(UserDao.class);
                userDao.insert(user);
                Integer id = user.getId();
                System.out.println(id);
            }
        }
    }

    @Test
    public void jdbcTest() throws Exception {
        Properties properties = new Properties();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("demo.properties");
        properties.load(inputStream);
        Class cla = Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(properties.getProperty("jdbc.url1"), properties.getProperty("jdbc.username"), properties.getProperty("jdbc.password"));
        PreparedStatement statement = connection.prepareStatement("");
        statement.setString(1, "");
        ResultSet resultSet = statement.executeQuery();
    }
}
