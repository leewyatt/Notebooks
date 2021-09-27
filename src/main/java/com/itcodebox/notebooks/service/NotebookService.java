package com.itcodebox.notebooks.service;

import com.itcodebox.notebooks.entity.Notebook;

import java.util.List;

/**
 * @author LeeWyatt
 */
public interface NotebookService extends CommonService<Notebook> {
    /**
     * 通过名字寻找笔记本
     * @param title 笔记本的名字
     * @return 笔记本
     */
    Notebook findByTitle( String title);

    /**
     * 查找全部的笔记本
     * @return 全部的笔记本
     */
    List<Notebook> findAll();

    /**
     *获取全部笔记的标题;用于首页展示
     * @return 获取全部笔记本的标题
     */
    List<String> getTitles();

}
