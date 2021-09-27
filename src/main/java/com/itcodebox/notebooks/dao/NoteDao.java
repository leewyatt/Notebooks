package com.itcodebox.notebooks.dao;

import com.itcodebox.notebooks.entity.Note;

import java.sql.Connection;
import java.util.List;

/**
 * @author LeeWyatt
 */
public interface NoteDao extends CommonDao<Note> {
    /**
     * @param conn      连接
     * @param chapterId 章节id
     * @return 章节下所有的笔记
     */
    List<Note> findAllByChapterId(Connection conn, Integer chapterId);

    /**
     * 删除指定章节下的所有笔记
     *
     * @param conn      连接
     * @param chapterId 章节id
     */
    void deleteAllByChapterId(Connection conn, Integer chapterId);

    /**
     * 删除指定笔记本下的所有笔记
     *
     * @param conn       连接
     * @param notebookId 笔记本id
     */
    void deleteAllByNotebookId(Connection conn, Integer notebookId);

    /**
     * 获取指定笔记本,指定章节下的全部标题
     *
     * @param conn         连接
     * @param notebookName 笔记本标题
     * @param chapterName  章节标题
     * @return
     */
    List<String> getTitles(Connection conn, String notebookName, String chapterName);

    /**
     * 根据标题查找
     */
    Note findByTitle(Connection conn,String noteName,Integer chapterId);

    Note findByTitles(Connection conn, String noteTile, String chapterTitle, String notebookTitle);

    List<String> getImageRecordsByNotebookId(Connection conn, int notebookId);

    List<String> getImageRecordsByChapterId(Connection conn, int chapterId);

    List<String> getImageRecordsByNoteId(Connection conn, int noteId);

}
