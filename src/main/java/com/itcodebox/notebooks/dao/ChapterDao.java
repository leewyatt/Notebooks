package com.itcodebox.notebooks.dao;

import com.itcodebox.notebooks.entity.Chapter;

import java.sql.Connection;
import java.util.List;

/**
 * @author LeeWyatt
 */
public interface ChapterDao extends CommonDao<Chapter>{
    /**
     * 获得指定的笔记本里的全部章节页
     * @param conn 连接
     * @param notebookId 书籍id
     * @return 指定书籍下的全部章节
     */
    List<Chapter> findAllByNotebookId(Connection conn, Integer notebookId);

    /**
     * 删除指定笔记本利的全部笔记
     * @param conn 连接
     * @param notebookId 书籍id
     */
    void deleteAllByNotebookId(Connection conn, Integer notebookId);

    /**
     *
     * @param conn
     * @param name
     * @param notebookId
     * @return
     */
    Chapter findByTitle(Connection conn, String name, Integer notebookId);

    /**
     * 获取笔记本下全部章节的标题
     * @param conn 连接
     * @param notebookName 笔记本标题
     * @return 全部章节标题
     */
    List<String> getTitles(Connection conn, String notebookName);
}
