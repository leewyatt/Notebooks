package com.itcodebox.notebooks.service.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.itcodebox.notebooks.dao.NoteDao;
import com.itcodebox.notebooks.dao.impl.NoteDaoImpl;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.service.DatabaseBasicService;
import com.itcodebox.notebooks.service.NoteService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author LeeWyatt
 */
public class  NoteServiceImpl implements NoteService {
    private final DatabaseBasicService databaseBasicService = ApplicationManager.getApplication().getService(DatabaseBasicService.class);
    private final NoteDao noteDao = NoteDaoImpl.getInstance();

    private NoteServiceImpl() {
    }

    public static NoteServiceImpl getInstance() {
        return ApplicationManager.getApplication().getService(NoteServiceImpl.class);
    }

    @Override
    public void insert(Note[] ary) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            noteDao.insert(conn, ary);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
    }

    @Override
    public Note insert(Note note) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return noteDao.insert(conn, note);
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
            noteDao.delete(conn, id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
    }

    @Override
    public void update(Note[] ary) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            noteDao.update(conn, ary);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
    }

    @Override
    public void update(Note note) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            noteDao.update(conn, note);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
    }

    @Override
    public Note findById(Integer id) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return noteDao.findById(conn, id);
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
            noteDao.exchangeShowOrder(conn, showOrder1, showOrder2);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
    }

    @Override
    public List<Note> findAllByChapterId(Integer chapterId) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return noteDao.findAllByChapterId(conn, chapterId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
        return null;
    }

    @Override
    public void deleteAllByChapterId(Integer chapterId) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            noteDao.deleteAllByChapterId(conn, chapterId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
    }

    @Override
    public void deleteAllByNotebookId(Integer notebookId) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            noteDao.deleteAllByNotebookId(conn, notebookId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
    }

    @Override
    public List<String> getTitles(String notebookTitle, String chapterTitle) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return noteDao.getTitles(conn, notebookTitle, chapterTitle);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
        return null;
    }

    @Override
    public Note findByTitle(String noteTitle, Integer chapterId) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return noteDao.findByTitle(conn, noteTitle,chapterId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
        return null;
    }

    @Override
    public Note findByTitles(String noteTile, String chapterTitle, String notebookTitle) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return noteDao.findByTitles(conn,noteTile ,chapterTitle,notebookTitle);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
        return null;
    }

    @Override
    public List<String> getImageRecordsByNotebookId(int notebookId) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return noteDao.getImageRecordsByNotebookId(conn,notebookId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
        return null;
    }

    @Override
    public List<String> getImageRecordsByChapterId(int chapterId) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return noteDao.getImageRecordsByChapterId(conn,chapterId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
        return null;
    }

    @Override
    public List<String> getImageRecordsByNoteId( int noteId) {
        Connection conn = null;
        try {
            conn = databaseBasicService.getConnection();
            return noteDao.getImageRecordsByNoteId(conn,noteId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            databaseBasicService.closeResource(conn, null, null);
        }
        return null;
    }

}
