package com.itcodebox.notebooks.service;

import com.itcodebox.notebooks.entity.SearchMode;
import com.itcodebox.notebooks.entity.SearchRecord;

import java.util.List;

/**
 * @author LeeWyatt
 */
public interface SearchRecordService {
    List<SearchRecord> searchKeywords(String keywords, SearchMode searchMode);
}
