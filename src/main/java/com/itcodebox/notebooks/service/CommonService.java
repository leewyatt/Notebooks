package com.itcodebox.notebooks.service;

/**
 * @author LeeWyatt
 */
public interface CommonService<T> {

    /**
     * 增: 将对象添加到数据库中
     */
    void insert( T[] ary);

    /**
     * 增加对象,并且返回带ID的对象
     */
    T insert( T t);

    /**
     * 删: 根据id,删除数据库中的一条记录
     */
    void delete( Integer id);

    /**
     * 根据id来修改数据
     *
     * @param ary  数据
     */
    void update( T[] ary);

    /**
     * 根据id来修改数据
     *
     * @param t    数据
     */
    void update( T t);

    /**
     * 查: 根据id,查询一条记录
     */
    T findById( Integer id);

    /**
     * 交换位置; show_order1和show_order2进行交换
     */
    void exchangeShowOrder( Integer showOrder1, Integer showOrder2);

}
