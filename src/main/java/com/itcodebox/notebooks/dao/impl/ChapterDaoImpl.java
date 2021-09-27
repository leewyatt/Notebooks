package com.itcodebox.notebooks.dao.impl;

import com.itcodebox.notebooks.dao.BaseDAO;
import com.itcodebox.notebooks.dao.ChapterDao;
import com.itcodebox.notebooks.entity.Chapter;

import java.sql.Connection;
import java.util.List;

/**
 * @author LeeWyatt
 */
public class ChapterDaoImpl extends BaseDAO<Chapter> implements ChapterDao {

    private ChapterDaoImpl() {
    }

    private static ChapterDaoImpl instance;

    public static synchronized ChapterDaoImpl getInstance() {
        if (instance == null) {
            instance = new ChapterDaoImpl();
        }
        return instance;
    }

    private static final String SELECT_SQL = "select id,notebook_id,title,create_time,update_time,show_order from chapter ";

    @Override
    public void insert(Connection conn, Chapter[] chapters) {
        if (chapters == null || chapters.length == 0) {
            return;
        }
        String sql = "insert into chapter(show_order,title,create_time,update_time,notebook_id) values(ifnull((select seq from sqlite_sequence where name='chapter'),0)+1,?,?,?,?)";
        int size = chapters.length;
        if (size == 1) {
            Chapter chapter = chapters[0];
            update(conn, sql, chapter.getTitle(), chapter.getCreateTime(), chapter.getUpdateTime(), chapter.getNotebookId());
        } else {
            Object[][] args = new Object[size][4];
            for (int i = 0; i < size; i++) {
                Chapter chapter = chapters[i];
                args[i][0] = chapter.getTitle();
                args[i][1] = chapter.getCreateTime();
                args[i][2] = chapter.getUpdateTime();
                args[i][3] = chapter.getNotebookId();
            }
            updateBatch(conn, sql, args);
        }
    }

    @Override
    public Chapter insert(Connection conn, Chapter chapter) {
        String sql = "insert into chapter(show_order,title,create_time,update_time,notebook_id) values(ifnull((select seq from sqlite_sequence where name='chapter'),0)+1,?,?,?,?);";
        update(conn, sql, chapter.getTitle(), chapter.getCreateTime(), chapter.getUpdateTime(), chapter.getNotebookId());
        String sqlSelect = SELECT_SQL + " where id=(select LAST_INSERT_ROWID()) limit 1;";
        return getBean(conn, sqlSelect);
    }

    @Override
    public void delete(Connection conn, Integer id) {
        String sql1 = "delete from note where chapter_id=?;";
        update(conn, sql1, id);
        String sql2 = "delete from chapter where id =?";
        update(conn, sql2, id);
    }

    /**
     * 改变显示顺序,需要同时修改2个对象,
     * 所以修改显示顺序 使用exchangeShowOrder即可,这里不做修改
     */
    @Override
    public void update(Connection conn, Chapter[] chapters) {
        if (chapters == null || chapters.length == 0) {
            return;
        }
        String sql = "update chapter set title=?,create_time=?,update_time=?,show_order=?,notebook_id=? where id =?";
        int size = chapters.length;
        if (size == 1) {
            Chapter chapter = chapters[0];
            update(conn, sql, chapter.getTitle(), chapter.getCreateTime(), chapter.getUpdateTime(), chapter.getShowOrder(), chapter.getNotebookId(), chapter.getId());
        } else {
            Object[][] args = new Object[size][6];
            for (int i = 0; i < size; i++) {
                Chapter chapter = chapters[i];
                args[i][0] = chapter.getTitle();
                args[i][1] = chapter.getCreateTime();
                args[i][2] = chapter.getUpdateTime();
                args[i][3] = chapter.getShowOrder();
                args[i][4] = chapter.getNotebookId();
                args[i][5] = chapter.getId();
            }
            updateBatch(conn, sql, args);
        }
    }

    @Override
    public void update(Connection conn, Chapter chapter) {
        String sql = "update chapter set title=?,create_time=?,update_time=?,show_order=?,notebook_id=? where id =?";
        update(conn, sql, chapter.getTitle(), chapter.getCreateTime(), chapter.getUpdateTime(), chapter.getShowOrder(), chapter.getNotebookId(), chapter.getId());
    }

    @Override
    public Chapter findById(Connection conn, Integer id) {
        String sql = SELECT_SQL + "where id =? limit 1";
        return getBean(conn, sql, id);

    }

    @Override
    public Chapter findByTitle(Connection conn, String title, Integer notebookId) {
        String sql = SELECT_SQL + " where notebook_id=? and title =? limit 1";
        return getBean(conn, sql, notebookId, title);
    }

    @Override
    public List<String> getTitles(Connection conn, String notebookTitle) {
        String sql = "select title from chapter where notebook_id=(select id from notebook where title=?)  order by show_order asc;";
        return queryTitleList(conn, sql, notebookTitle);
    }

    @Override
    public void exchangeShowOrder(Connection conn, Integer showOrder1, Integer showOrder2) {
        String sql = "UPDATE chapter " +
                "SET show_order = (CASE WHEN show_order = ? THEN ? " +
                "                  WHEN show_order = ?  THEN ? END )" +
                "where show_order=? or show_order=?";
        update(conn, sql, showOrder1, showOrder2, showOrder2, showOrder1, showOrder1, showOrder2);
    }


    @Override
    public List<Chapter> findAllByNotebookId(Connection conn, Integer notebookId) {
        String sql = SELECT_SQL + "where notebook_id=? order by show_order asc";
        return queryList(conn, sql, notebookId);
    }

    @Override
    public void deleteAllByNotebookId(Connection conn, Integer notebookId) {
        String sql = "delete from chapter where notebook_id =?";
        update(conn, sql, notebookId);
    }

}
