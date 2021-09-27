package com.itcodebox.notebooks.service;

import com.itcodebox.notebooks.entity.Note;

import java.util.List;

/**
 * @author LeeWyatt
 */
public interface NoteService extends CommonService<Note> {
    /**
     * @param chapterId 章节id
     * @return 章节下所有的笔记
     */
    List<Note> findAllByChapterId(Integer chapterId);

    /**
     * 删除指定章节下的所有笔记
     *
     * @param chapterId 章节id
     */
    void deleteAllByChapterId(Integer chapterId);

    /**
     * 删除指定笔记本下的所有笔记
     *
     * @param notebookId 笔记本id
     */
    void deleteAllByNotebookId(Integer notebookId);

    /**
     * 获取指定笔记本,指定章节下的全部标题
     *
     * @param notebookTitle 笔记本标题
     * @param chapterTitle  章节标题 (注意,笔记本标题,全局不重复,. 而章节标题只是同一笔记本下不重复)
     * @return 同一章节下全部标题
     */
    List<String> getTitles(String notebookTitle, String chapterTitle);

    Note findByTitle(String noteName, Integer chapterId);

    Note findByTitles(String noteTile, String chapterTitle, String notebookTitle);


    List<String> getImageRecordsByNotebookId( int notebookId);

    List<String> getImageRecordsByChapterId( int chapterId);

    List<String> getImageRecordsByNoteId( int noteId);
}