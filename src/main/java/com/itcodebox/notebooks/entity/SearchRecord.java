package com.itcodebox.notebooks.entity;

import java.util.Objects;

/**
 * @author LeeWyatt
 */
public class SearchRecord {
    private String notebookTitle;
    private Integer notebookId;
    private String chapterTitle;
    private Integer chapterId;
    private String noteTitle;
    private Integer noteId;
    private String type;
    private String content;
    private String description;

    public SearchRecord() {
    }

    public String getPath(String delimiter) {
        StringBuilder strBuilder = new StringBuilder(128);
        if (notebookTitle != null) {
            strBuilder.append(notebookTitle);
        }
        if (chapterTitle != null) {
            strBuilder.append(delimiter).append(chapterTitle);
        }
        if (noteTitle != null) {
            strBuilder.append(delimiter).append(noteTitle);
        }

        return strBuilder.toString();
    }

    @Override
    public String toString() {
        return getPath(" ");
    }

    public String getInfos() {
        StringBuilder strBuilder = new StringBuilder(512);
        if (notebookTitle != null) {
            strBuilder.append(notebookTitle);
        }
        if (chapterTitle != null) {
            strBuilder.append(" ").append(chapterTitle);
        }
        if (noteTitle != null) {
            strBuilder.append(" ").append(noteTitle);
        }
        if (content != null) {
            strBuilder.append(" ").append(content);
        }
        if (description != null) {
            strBuilder.append(" ").append(description);
        }

        return strBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchRecord that = (SearchRecord) o;
        return Objects.equals(notebookId, that.notebookId) && Objects.equals(chapterId, that.chapterId) && Objects.equals(noteId, that.noteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notebookId, chapterId, noteId);
    }

    public String getNotebookTitle() {
        return notebookTitle;
    }

    public void setNotebookTitle(String notebookTitle) {
        this.notebookTitle = notebookTitle;
    }

    public Integer getNotebookId() {
        return notebookId;
    }

    public void setNotebookId(Integer notebookId) {
        this.notebookId = notebookId;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public Integer getChapterId() {
        return chapterId;
    }

    public void setChapterId(Integer chapterId) {
        this.chapterId = chapterId;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public Integer getNoteId() {
        return noteId;
    }

    public void setNoteId(Integer noteId) {
        this.noteId = noteId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
