package com.itcodebox.notebooks.dao;

import com.itcodebox.notebooks.entity.SearchRecord;
import com.itcodebox.notebooks.entity.SearchMode;

import java.sql.Connection;
import java.util.List;

/**
 * @author LeeWyatt
 */
public interface SearchRecordDao {
    /**
     * 根据指定的范围和关键字.搜索结果
     *
     * @param conn 连接
     * @param keywords 关键字
     * @param searchMode  指定搜索模式(搜索范围)
     * @return 搜索结果
     */
    List<SearchRecord> searchKeywords(Connection conn, String keywords, SearchMode searchMode);
}
