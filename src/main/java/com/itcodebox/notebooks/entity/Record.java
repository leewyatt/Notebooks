package com.itcodebox.notebooks.entity;

import org.jetbrains.annotations.NotNull;

/**
 * @author LeeWyatt
 */
public class Record implements Comparable<Record>{
    protected Integer id;
    protected String title;
    protected Integer showOrder;
    protected Long createTime;
    protected Long updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getShowOrder() {
        return showOrder;
    }

    public void setShowOrder(Integer showOrder) {
        this.showOrder = showOrder;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 为了让搜索更加直观,只返回标题字符串
     * @return 支持搜索的内容
     */
    @Override
    public String toString() {
        return title;
    }

    @Override
    public int compareTo(@NotNull Record o) {
        return Integer.compare(this.showOrder,o.getShowOrder());
    }
}
