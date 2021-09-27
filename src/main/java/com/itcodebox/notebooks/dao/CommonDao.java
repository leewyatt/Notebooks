package com.itcodebox.notebooks.dao;

import java.sql.Connection;

/**
 * @author LeeWyatt
 */
public interface CommonDao<T> {

    /**
     * 增: 将对象添加到数据库中
     */
    void insert(Connection conn, T[] ary);

    /**
     * 增加对象,并且返回带ID的对象
     */
    T insert(Connection conn, T t);

    /**
     * 删: 根据id,删除数据库中的一条记录
     */
    void delete(Connection conn, Integer id);

    /**
     * 根据id来修改数据
     * @param conn 连接
     * @param ary 数据
     */
    void update(Connection conn, T[] ary);

    /**
     * 根据id来修改数据
     * @param conn 连接
     * @param t 数据
     */
    void update(Connection conn, T t);

    /**
     * 查: 根据id,查询一条记录
     */
    T findById(Connection conn, Integer id);

    /**
     * 交换位置; show_order1和show_order2进行交换
     */
    void exchangeShowOrder(Connection conn,Integer showOrder1,Integer showOrder2);


}
