package com.ja.plugin;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;

import java.util.Properties;

public class Page implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        return null;
    }

    @Override
    public Object plugin(Object target) {

        return null;
    }

    @Override
    public void setProperties(Properties properties) {


    }
}
