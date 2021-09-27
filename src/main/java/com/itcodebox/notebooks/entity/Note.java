/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itcodebox.notebooks.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itcodebox.notebooks.utils.ImageRecordUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author LeeWyatt
 */
public class Note extends Record {
    private Integer chapterId;
    private Integer notebookId;
    private String content;
    private String description;
    private String source;
    private int offsetStart;
    private int offsetEnd;
    private String type;
    private String imageRecords;
    public String getImageRecords() {
        return imageRecords;
    }

    public void setImageRecords(String imageRecords) {
        this.imageRecords = imageRecords;
    }

    @JsonIgnore
    private List<ImageRecord> imageRecordList;

    @JsonIgnore
    public List<ImageRecord> getImageRecordList() {
        return ImageRecordUtil.convertToList(imageRecords);
    }

    public Note() {
    }

    public Note(String title, Integer notebookId, Integer chapterId, Long createTime) {
        this(title, notebookId, chapterId, createTime, createTime);
    }

    public Note(String title, Integer notebookId, Integer chapterId, Long createTime, Long updateTime) {
        this.title = title;
        this.notebookId = notebookId;
        this.chapterId = chapterId;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    //public List<ImageRecord> getImageRecords() {
    //    return imageRecords;
    //}
    //
    //public void setImageRecords(List<ImageRecord> imageRecords) {
    //    this.imageRecords = imageRecords;
    //}

    public Integer getNotebookId() {
        return notebookId;
    }

    public void setNotebookId(Integer notebookId) {
        this.notebookId = notebookId;
    }

    public Integer getChapterId() {
        return chapterId;
    }

    public void setChapterId(Integer chapterId) {
        this.chapterId = chapterId;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOffsetStart() {
        return offsetStart;
    }

    public void setOffsetStart(int offsetStart) {
        this.offsetStart = offsetStart;
    }

    public int getOffsetEnd() {
        return offsetEnd;
    }

    public void setOffsetEnd(int offsetEnd) {
        this.offsetEnd = offsetEnd;
    }


    /**
     * 遇见了一个坑;重写equals方法,忘记了带上父类的属性
     * 由于子类的属性完全相同. 交所以导致同一章节下的很多note都返回true. 导致List的indexOf方法找不到正确的结果
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Note note = (Note) o;
        return Objects.equals(id, note.id) && Objects.equals(title, note.title) && Objects.equals(showOrder, note.showOrder) && Objects.equals(createTime, note.createTime) && Objects.equals(updateTime, note.updateTime) && Objects.equals(chapterId, note.chapterId) && Objects.equals(notebookId, note.notebookId) && Objects.equals(content, note.content) && Objects.equals(description, note.description) && Objects.equals(source, note.source) && Objects.equals(type, note.type) && (offsetStart == note.offsetStart) && (offsetEnd == note.offsetEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, showOrder, createTime, updateTime, chapterId, notebookId, content, description, source, offsetStart, offsetEnd, type);
    }

}
