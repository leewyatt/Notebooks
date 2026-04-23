package com.itcodebox.notebooks.service.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.itcodebox.notebooks.dao.SearchRecordDao;
import com.itcodebox.notebooks.dao.impl.SearchRecordDaoImpl;
import com.itcodebox.notebooks.entity.SearchMode;
import com.itcodebox.notebooks.entity.SearchRecord;
import com.itcodebox.notebooks.service.DatabaseBasicService;
import com.itcodebox.notebooks.service.SearchRecordService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author LeeWyatt
 */
public class SearchRecordServiceImpl implements SearchRecordService {
    private static final Logger LOG = Logger.getInstance(SearchRecordServiceImpl.class);
    private final DatabaseBasicService databaseBasicService = ApplicationManager.getApplication().getService(DatabaseBasicService.class);
    private final SearchRecordDao searchRecordDao = SearchRecordDaoImpl.getInstance();

    protected SearchRecordServiceImpl() {
    }

    public static SearchRecordServiceImpl getInstance() {
        return ApplicationManager.getApplication().getService(SearchRecordServiceImpl.class);
    }

    @Override
    public List<SearchRecord> searchKeywords(String keywords, SearchMode searchMode) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return searchRecordDao.searchKeywords(conn, keywords,searchMode);
        } catch (SQLException throwables) {
            LOG.warn("Failed to search records by keywords", throwables);
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
        return null;
    }
}
