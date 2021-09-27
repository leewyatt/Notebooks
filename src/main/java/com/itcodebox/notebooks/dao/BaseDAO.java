package com.itcodebox.notebooks.dao;

import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 封装了针对于数据的通用操作
 * DAO: database access object: 数据库访问对象
 * @param <T>
 */
@SuppressWarnings("unchecked")
public abstract class BaseDAO<T> {
    private Class<T> clazz;
    protected QueryRunner queryRunner = new QueryRunner();


    public BaseDAO(){
        Class clazzTemp = this.getClass();//获取子类的类型
        //获取父类的类型
        //getGenericSuperclass() 代表获取父类的类型
        //parameterizedType 代表的是带泛型的类型
        ParameterizedType parameterizedType= (ParameterizedType) clazzTemp.getGenericSuperclass();
        //获取泛型类型的数组
        Type[] types = parameterizedType.getActualTypeArguments();
        this.clazz= (Class<T>) types[0];
    }

    //除开增删改查. 还需要sql语句查询比如总共有多少个用户之类的通用方法
    public <E> E getValue(Connection conn,String sql ,Object...args){
        E result = null;
        ScalarHandler<E> handler = new ScalarHandler<E>();
        try {
            result = queryRunner.query(conn, sql, handler,args);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }



    //查询多个对象--ver3.0
    public List<T> queryList(Connection conn, String sql, Object... objs) {
        List<T> list = null;
        //开启驼峰映射 因为数据库是 create_time .而java 类是createTime
        BeanProcessor bean = new GenerousBeanProcessor();
        RowProcessor processor = new BasicRowProcessor(bean);
        BeanListHandler<T> handler = new BeanListHandler<T>(clazz,processor);
        try {
            list = queryRunner.query(conn, sql, handler, objs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //List<Object>每个列数据类型不同
    public List<String> queryTitleList(Connection conn, String sql, Object... objs){
        List<String> list = null;
        ColumnListHandler<String> handler =new ColumnListHandler<String>("title");
        try {
            list = queryRunner.query(conn, sql, handler,objs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //List<Object>每个列数据类型不同
    public List<String> queryImageRecords(Connection conn, String sql, Object... objs){
        List<String> list = null;
        ColumnListHandler<String> handler =new ColumnListHandler<String>("image_records");
        try {
            list = queryRunner.query(conn, sql, handler,objs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    //查询单个对象--ver3.0
    public T getBean(Connection conn, String sql, Object... objs) {
        T t = null;
        try {
            //开启驼峰映射
            BeanProcessor bean = new GenerousBeanProcessor();
            RowProcessor processor = new BasicRowProcessor(bean);
            t = queryRunner.query(conn,sql,new BeanHandler<T>(clazz,processor),objs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return t;
    }

    //增删改 通用--ver3.0
    public Integer update(Connection conn, String sql, Object... args) {
        int count = 0;
        try {
            count = queryRunner.update(conn, sql, args);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    //修改多个对象的id;
    public void updateBatch(Connection conn, String sql, Object[][] args){
        try {
            queryRunner.batch(conn, sql, args);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
