package com.ja;

import com.ja.dao.UserDao;
import com.ja.entity.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Test2 {

    private static final Log log = LogFactory.getLog(Test2.class);

    @Test
    public void testMultipleDataSources() throws IOException {
        SqlSession sqlSession;
        for (int i = 0; i < 10; i++) {
            int sex = (int) (Math.random() * 2);
            if (sex == 1) {
                sqlSession = this.getSqlSession("test1");
            } else {
                sqlSession = this.getSqlSession("test2");
            }
            User user = new User();
            user.setName("小明" + i);
            user.setSex(String.valueOf(sex));
            user.setAge((int) (Math.random() * 30));
            UserDao userDao = sqlSession.getMapper(UserDao.class);
            userDao.insert(user);
            Integer id = user.getId();
            System.out.println(id);
            sqlSession.close();
        }
    }


    Map<String, SqlSessionFactory> map = new HashMap<>();

    public SqlSession getSqlSession(String id) throws IOException {
        SqlSessionFactory sqlSessionFactory = map.get(id);
        SqlSession sqlSession;
        if (sqlSessionFactory == null) {
            String resource = "mybatis/mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, id);
            map.put(id, sqlSessionFactory);
        }
        sqlSession = sqlSessionFactory.openSession(true);
        return sqlSession;
    }

}
