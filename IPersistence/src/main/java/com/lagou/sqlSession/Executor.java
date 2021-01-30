package com.lagou.sqlSession;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;

import java.beans.IntrospectionException;
import java.sql.SQLException;
import java.util.List;

public interface Executor {

    public <E> List<E> query(Configuration configuration,MappedStatement mappedStatement,Object... params) throws Exception;

    public Integer update(Configuration configuration,MappedStatement mappedStatement,Object... params) throws Exception;

    public Integer insert (Configuration configuration,MappedStatement mappedStatement,Object... params) throws SQLException, IntrospectionException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchFieldException, Exception;

    public Integer delete (Configuration configuration,MappedStatement mappedStatement,Object... params) throws Exception;

}
