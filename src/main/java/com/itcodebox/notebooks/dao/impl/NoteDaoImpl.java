package com.itcodebox.notebooks.dao.impl;

import com.itcodebox.notebooks.dao.BaseDAO;
import com.itcodebox.notebooks.dao.NoteDao;
import com.itcodebox.notebooks.entity.Note;

import java.sql.Connection;
import java.util.List;

/**
 * @author LeeWyatt
 */
public class NoteDaoImpl extends BaseDAO<Note> implements NoteDao {
    private static NoteDaoImpl instance;

    private NoteDaoImpl() {
    }

    public static synchronized NoteDaoImpl getInstance() {
        if (instance == null) {
            instance = new NoteDaoImpl();
        }
        return instance;
    }

    private static final String SELECT_SQL = "select id,title,chapter_id,notebook_id,create_time,update_time,show_order,content,description,source,type,offset_start,offset_end,image_records from note ";

    @Override
    public void insert(Connection conn, Note[] notes) {
        if (notes == null || notes.length == 0) {
            return;
        }
        String sql = "insert into note(show_order,title,chapter_id,notebook_id,create_time,update_time,content,description,source,type,offset_start,offset_end,image_records) values(ifnull((select seq from sqlite_sequence where name='note'),0)+1,?,?,?,?,?,ifnull(?,''),ifnull(?,''),ifnull(?,''),ifnull(?,''),?,?,?)";
        int size = notes.length;
        if (size == 1) {
            Note note = notes[0];
            update(conn, sql, note.getTitle(), note.getChapterId(), note.getNotebookId(), note.getCreateTime(), note.getUpdateTime(), note.getContent(), note.getDescription(), note.getSource(), note.getType(),note.getOffsetStart(),note.getOffsetEnd(),note.getImageRecords());
        } else {
            Object[][] args = new Object[size][12];
            for (int i = 0; i < size; i++) {
                Note note = notes[i];
                args[i][0] = note.getTitle();
                args[i][1] = note.getChapterId();
                args[i][2] = note.getNotebookId();
                args[i][3] = note.getCreateTime();
                args[i][4] = note.getUpdateTime();
                args[i][5] = note.getContent();
                args[i][6] = note.getDescription();
                args[i][7] = note.getSource();
                args[i][8] = note.getType();
                args[i][9] = note.getOffsetStart();
                args[i][10] = note.getOffsetEnd();
                args[i][11] = note.getImageRecords();
            }
            updateBatch(conn, sql, args);
        }
    }

    @Override
    public Note insert(Connection conn, Note note) {
        String sql = "insert into note(show_order,title,chapter_id,notebook_id,create_time,update_time,content,description,source,type,offset_start,offset_end,image_records) values(ifnull((select seq from sqlite_sequence where name='note'),0)+1,?,?,?,?,?,ifnull(?,''),ifnull(?,''),ifnull(?,''),ifnull(?,''),?,?,?); ";
        update(conn, sql, note.getTitle(), note.getChapterId(), note.getNotebookId(), note.getCreateTime(), note.getUpdateTime(), note.getContent(), note.getDescription(), note.getSource(), note.getType(),note.getOffsetStart(),note.getOffsetEnd(),note.getImageRecords());
        String sqlSelect = SELECT_SQL + " where id=(select LAST_INSERT_ROWID()) limit 1;";
        return getBean(conn, sqlSelect);
    }

    @Override
    public void delete(Connection conn, Integer id) {
        String sql = "delete from note where id =?";
        update(conn, sql, id);
    }

    @Override
    public void update(Connection conn, Note[] notes) {
        if (notes == null || notes.length == 0) {
            return;
        }
        String sql = "update note set title=? ,show_order=?,chapter_id=?,notebook_id=?,create_time=?,update_time=?,content=?,description=?,source=?,type=?,offset_start=?,offset_end=?,image_records=? where id =?";
        int size = notes.length;
        if (size == 1) {
            Note note = notes[0];
            update(conn, sql, note.getTitle(), note.getShowOrder(), note.getChapterId(), note.getNotebookId(), note.getCreateTime(), note.getUpdateTime(), note.getContent(), note.getDescription(), note.getSource(), note.getType(),note.getOffsetStart(),note.getOffsetEnd(),note.getImageRecords(), note.getId());
        } else {
            Object[][] args = new Object[size][14];
            for (int i = 0; i < size; i++) {
                Note note = notes[i];
                args[i][0] = note.getTitle();
                args[i][1] = note.getShowOrder();
                args[i][2] = note.getChapterId();
                args[i][3] = note.getNotebookId();
                args[i][4] = note.getCreateTime();
                args[i][5] = note.getUpdateTime();
                args[i][6] = note.getContent();
                args[i][7] = note.getDescription();
                args[i][8] = note.getSource();
                args[i][9] = note.getType();
                args[i][10] = note.getOffsetStart();
                args[i][11] = note.getOffsetEnd();
                args[i][12] = note.getImageRecords();
                args[i][13] = note.getId();
            }
            updateBatch(conn, sql, args);
        }
    }

    @Override
    public void update(Connection conn, Note note) {
        String sql = "update note set title=?,show_order=?,chapter_id=?,notebook_id=?,create_time=?,update_time=?,content=?,description=?,source=?,type=?,offset_start=?,offset_end=?,image_records=? where id =?";
        update(conn, sql, note.getTitle(), note.getShowOrder(), note.getChapterId(), note.getNotebookId(), note.getCreateTime(), note.getUpdateTime(), note.getContent(), note.getDescription(), note.getSource(), note.getType(), note.getOffsetStart(),note.getOffsetEnd(),note.getImageRecords(),note.getId());
    }

    @Override
    public Note findById(Connection conn, Integer id) {
        String sql = SELECT_SQL + "where id =? limit 1";
        return getBean(conn, sql, id);
    }

    @Override
    public void exchangeShowOrder(Connection conn, Integer showOrder1, Integer showOrder2) {
        String sql = "UPDATE note " +
                "SET show_order = (CASE WHEN show_order = ? THEN ? " +
                "                  WHEN show_order = ?  THEN ? END )" +
                "where show_order=? or show_order=?";
        update(conn, sql, showOrder1, showOrder2, showOrder2, showOrder1, showOrder1, showOrder2);

    }

    @Override
    public List<Note> findAllByChapterId(Connection conn, Integer chapterId) {
        String sql = SELECT_SQL + "where chapter_id=? order by show_order asc";
        return queryList(conn, sql, chapterId);
    }

    @Override
    public void deleteAllByChapterId(Connection conn, Integer chapterId) {
        String sql = "delete from note where chapter_id =?";
        update(conn, sql, chapterId);
    }

    @Override
    public void deleteAllByNotebookId(Connection conn, Integer notebookId) {
        String sql = "delete from note where notebook_id =?";
        update(conn, sql, notebookId);
    }

    @Override
    public List<String> getTitles(Connection conn, String notebookTitle, String chapterTitle) {
        String sql = "select distinct title from note " +
                "where notebook_id=(select id from notebook where title=?) " +
                "and " +
                "chapter_id=(select id from chapter where title=? and notebook_id=(select id from notebook where title=?))  order by show_order asc;";
        return queryTitleList(conn, sql, notebookTitle, chapterTitle,notebookTitle);
    }

    @Override
    public Note findByTitle(Connection conn, String noteName, Integer chapterId) {
        String sql = SELECT_SQL + " where chapter_id=? and title =? limit 1";
        return getBean(conn, sql, chapterId, noteName);
    }

    @Override
    public Note findByTitles(Connection conn, String noteTile, String chapterTitle, String notebookTitle) {
        String sql = SELECT_SQL +
                " where notebook_id=(select id from notebook where title=?) " +
                "and " +
                "chapter_id=(select id from chapter where title=? and notebook_id=(select id from notebook where title=?)) " +
                "and " +
                "title =? limit 1";
        return getBean(conn, sql, notebookTitle,chapterTitle,notebookTitle, noteTile);
    }

    @Override
    public List<String> getImageRecordsByNotebookId(Connection conn, int notebookId) {
        String sql = "select image_records from note where notebook_id=?";
        return queryImageRecords(conn,sql,notebookId);
    }

    @Override
    public List<String> getImageRecordsByChapterId(Connection conn, int chapterId) {
            String sql = "select image_records from note where chapter_id=?";
            return queryImageRecords(conn,sql,chapterId);
    }

    @Override
    public List<String> getImageRecordsByNoteId(Connection conn, int noteId) {
        String sql = "select image_records from note where id=?";
        return queryImageRecords(conn,sql,noteId);
    }

}
