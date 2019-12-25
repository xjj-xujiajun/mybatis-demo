package com.ja.entity;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;

public class Configuration1 extends Configuration {

    @Override
    public void setDefaultExecutorType(ExecutorType defaultExecutorType) {
        super.setDefaultExecutorType(null);
    }
}
