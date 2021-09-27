package com.itcodebox.notebooks.service;

import com.itcodebox.notebooks.entity.Chapter;

import java.util.List;

/**
 * @author LeeWyatt
 */
public interface ChapterService extends CommonService<Chapter> {

    /**
     * 获得指定的笔记本里的全部章节页
     *
     * @param notebookId 书籍id
     * @return 指定书籍下的全部章节
     */
    List<Chapter> findAllByNotebookId(Integer notebookId);

    /**
     * 删除指定笔记本利的全部笔记
     *
     * @param notebookId 书籍id
     */
    void deleteAllByNotebookId(Integer notebookId);

    /**
     * @param title
     * @param notebookId
     * @return
     */
    Chapter findByTitle(String title, Integer notebookId);

    /**
     * 获取笔记本下全部章节的标题
     *
     * @param notebookTitle 笔记本标题
     * @return 全部章节标题
     */
    List<String> getTitles(String notebookTitle);
}
