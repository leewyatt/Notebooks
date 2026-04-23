package com.itcodebox.notebooks.service.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.itcodebox.notebooks.dao.NotebookDao;
import com.itcodebox.notebooks.dao.impl.NotebookDaoImpl;
import com.itcodebox.notebooks.entity.Notebook;
import com.itcodebox.notebooks.service.DatabaseBasicService;
import com.itcodebox.notebooks.service.NotebookService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author LeeWyatt
 */
public class NotebookServiceImpl implements NotebookService {
    private static final Logger LOG = Logger.getInstance(NotebookServiceImpl.class);
    private final DatabaseBasicService databaseBasicService = ApplicationManager.getApplication().getService(DatabaseBasicService.class);
    private final NotebookDao notebookDao = NotebookDaoImpl.getInstance();

    public static NotebookServiceImpl getInstance() {
        return ApplicationManager.getApplication().getService(NotebookServiceImpl.class);
    }

    private NotebookServiceImpl() {
    }

    @Override
    public void insert(Notebook[] ary) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            notebookDao.insert(conn, ary);
        } catch (SQLException throwables) {
            LOG.warn("Failed to batch insert notebooks", throwables);
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
    }

    @Override
    public Notebook insert(Notebook notebook) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return notebookDao.insert(conn, notebook);
        } catch (SQLException throwables) {
            LOG.warn("Failed to insert notebook", throwables);
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
            notebookDao.delete(conn, id);
        } catch (SQLException throwables) {
            LOG.warn("Failed to delete notebook by id", throwables);
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
    }

    @Override
    public void update(Notebook[] ary) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            notebookDao.update(conn, ary);
        } catch (SQLException throwables) {
            LOG.warn("Failed to batch update notebooks", throwables);
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
    }

    @Override
    public void update(Notebook notebook) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            notebookDao.update(conn, notebook);
        } catch (SQLException throwables) {
            LOG.warn("Failed to update notebook", throwables);
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
    }

    @Override
    public Notebook findById(Integer id) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return notebookDao.findById(conn, id);
        } catch (SQLException throwables) {
            LOG.warn("Failed to find notebook by id", throwables);
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
            notebookDao.exchangeShowOrder(conn, showOrder1, showOrder2);
        } catch (SQLException throwables) {
            LOG.warn("Failed to exchange notebook show order", throwables);
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
    }

    @Override
    public Notebook findByTitle(String title) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return notebookDao.findByTitle(conn, title);
        } catch (SQLException throwables) {
            LOG.warn("Failed to find notebook by title", throwables);
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
        return null;
    }

    @Override
    public List<Notebook> findAll() {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return notebookDao.findAll(conn);
        } catch (SQLException throwables) {
            LOG.warn("Failed to find all notebooks", throwables);
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
        return null;
    }

    @Override
    public List<String> getTitles() {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return notebookDao.getTitles(conn);
        } catch (SQLException throwables) {
            LOG.warn("Failed to get notebook titles", throwables);
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
        return null;
    }

}
