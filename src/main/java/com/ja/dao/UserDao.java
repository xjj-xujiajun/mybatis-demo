package com.ja.dao;

import com.ja.entity.User;

import java.util.List;

public interface UserDao {

    User get(Integer id);

    List<User> findList(User user);

//    @Insert("insert into user(" +
//            "            name," +
//            "            sex," +
//            "            age" +
//            "        )values(" +
//            "            #{name}," +
//            "            #{sex}," +
//            "            #{age}" +
//            "        );")
//    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insert(User user);
}
