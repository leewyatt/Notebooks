package com.itcodebox.notebooks.service.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.itcodebox.notebooks.dao.ChapterDao;
import com.itcodebox.notebooks.dao.impl.ChapterDaoImpl;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.service.ChapterService;
import com.itcodebox.notebooks.service.DatabaseBasicService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author LeeWyatt
 */
public class ChapterServiceImpl implements ChapterService {
    private final DatabaseBasicService databaseBasicService = ApplicationManager.getApplication().getService(DatabaseBasicService.class);
    private final ChapterDao chapterDao = ChapterDaoImpl.getInstance();

    private ChapterServiceImpl() {
    }

    public static ChapterServiceImpl getInstance() {
       return ApplicationManager.getApplication().getService(ChapterServiceImpl.class);
        //return ApplicationManager.getApplication().getService(ChapterServiceImpl.class);
    }

    @Override
    public List<Chapter> findAllByNotebookId(Integer notebookId) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return chapterDao.findAllByNotebookId(conn, notebookId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
        return null;
    }

    @Override
    public void deleteAllByNotebookId(Integer notebookId) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            chapterDao.deleteAllByNotebookId(conn, notebookId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
    }

    @Override
    public Chapter findByTitle(String title, Integer notebookId) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return chapterDao.findByTitle(conn, title, notebookId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
        return null;
    }

    @Override
    public List<String> getTitles(String notebookTitle) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return chapterDao.getTitles(conn, notebookTitle);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
        return null;
    }

    @Override
    public void insert(Chapter[] ary) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            chapterDao.insert(conn, ary);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
    }

    @Override
    public Chapter insert(Chapter chapter) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return chapterDao.insert(conn, chapter);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
        return null;
    }

    @Override
    public void delete(Integer id) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            chapterDao.delete(conn, id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
    }

    @Override
    public void update(Chapter[] ary) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            chapterDao.update(conn, ary);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
    }

    @Override
    public void update(Chapter chapter) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            chapterDao.update(conn, chapter);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
    }

    @Override
    public Chapter findById(Integer id) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return chapterDao.findById(conn, id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
        return null;
    }

    @Override
    public void exchangeShowOrder(Integer showOrder1, Integer showOrder2) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            chapterDao.exchangeShowOrder(conn, showOrder1, showOrder2);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
    }
}
