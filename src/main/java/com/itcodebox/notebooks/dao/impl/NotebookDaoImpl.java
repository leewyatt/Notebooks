package com.itcodebox.notebooks.dao.impl;

import com.intellij.openapi.diagnostic.Logger;
import com.itcodebox.notebooks.dao.BaseDAO;
import com.itcodebox.notebooks.dao.NotebookDao;
import com.itcodebox.notebooks.entity.Notebook;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author LeeWyatt
 */
public class NotebookDaoImpl extends BaseDAO<Notebook> implements NotebookDao {

    private static final Logger LOG = Logger.getInstance(NotebookDaoImpl.class);

    private static NotebookDaoImpl instance;

    private NotebookDaoImpl() {
    }

    public static synchronized NotebookDaoImpl getInstance() {
        if (instance == null) {
            instance = new NotebookDaoImpl();
        }
        return instance;
    }

    private static final String SELECT_SQL = "select id,title,show_order,create_time,update_time from notebook ";

    @Override
    public void insert(Connection conn, Notebook[] notebooks) {
        if (notebooks == null || notebooks.length == 0) {
            return;
        }
        //数据库中存在自增列后，SQLite 就会创建一个 sqlite_sequence 表。所有表的自增列都共享这个表，sqlite_sequence 分别维护着每个自增列的当前值，所以自增列的计数也是单独的。
        String sql = "insert into notebook(show_order,title,create_time,update_time) values(ifnull((select seq from sqlite_sequence where name='notebook'),0)+1,?,?,?)";
        int size = notebooks.length;
        if (size == 1) {
            Notebook notebook = notebooks[0];
            update(conn, sql, notebook.getTitle(), notebook.getCreateTime(), notebook.getUpdateTime());
        } else {
            Object[][] args = new Object[size][3];
            for (int i = 0; i < size; i++) {
                Notebook notebook = notebooks[i];
                args[i][0] = notebook.getTitle();
                args[i][1] = notebook.getCreateTime();
                args[i][2] = notebook.getUpdateTime();
            }
            updateBatch(conn, sql, args);
        }
    }

    /**
     * LAST_INSERT_ROWID 返回最后一次insert的rowid,如果没有插入就返回0 (DB session断开后也返回0, 是保存在进程的内存中)
     * <p>
     * LAST_INSERT_ROWID 是与table无关的，如果向表a插入数据后，再向表b插入数据，LAST_INSERT_ROWID会改变。
     * 在多用户交替插入数据的情况下max(id)显然不能用。这时就该使用LAST_INSERT_ROWID了，
     * 因为LAST_INSERT_ROWID是基于Connection的，
     * 只要每个线程都使用独立的 Connection对象，
     * LAST_INSERT_ROWID函数将返回该Connection对AUTO_INCREMENT列最新的insert or update 操作生成的第一个record的ID。
     * 这个值不能被其它客户端（Connection）影响，保证了你能够找回自己的 ID 而不用担心其它客户端的活动，
     * 而且不需要加锁。使用单INSERT语句插入多条记录, LAST_INSERT_ROWID返回一个列表。
     *
     * @param conn     连接
     * @param notebook 笔记本
     * @return 笔记本(带id, show_order)
     */
    @Override
    public Notebook insert(Connection conn, Notebook notebook) {
        String sqlInsert = "insert into notebook(show_order,title,create_time,update_time) values(ifnull((select seq from sqlite_sequence where name='notebook'),0)+1,?,?,?);";
        update(conn, sqlInsert, notebook.getTitle(), notebook.getCreateTime(), notebook.getUpdateTime());
        //之前的版本, 是不太推荐的 where id =(select max(id) from notebook) limit 1;
        String sqlSelect = SELECT_SQL + " where id=(select LAST_INSERT_ROWID()) limit 1;";
        return getBean(conn, sqlSelect);

    }

    @Override
    public void delete(Connection conn, Integer id) {
        // Transactional cascade: notes -> chapters -> notebook. Without the
        // transaction, a failure between any two steps leaves orphan rows
        // (chapters with no parent notebook, etc.) and the UI shows stale
        // children that can't be re-selected.
        //
        // NOTE: we intentionally bypass BaseDAO.update(conn, sql) here because
        // that helper swallows SQLException internally — which would defeat
        // rollback. queryRunner.update(conn, sql) propagates the exception so
        // the catch block actually runs.
        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            queryRunner.update(conn, "delete from note where notebook_id=?;", id);
            queryRunner.update(conn, "delete from chapter where notebook_id=?;", id);
            queryRunner.update(conn, "delete from notebook where id=?;", id);
            conn.commit();
        } catch (SQLException e) {
            LOG.warn("Failed to delete notebook " + id + "; rolling back", e);
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                LOG.warn("Rollback failed after notebook delete error", rollbackEx);
            }
        } finally {
            try {
                conn.setAutoCommit(originalAutoCommit);
            } catch (SQLException restoreEx) {
                LOG.warn("Failed to restore autoCommit after notebook delete", restoreEx);
            }
        }
    }

    @Override
    public void update(Connection conn, Notebook[] notebooks) {
        if (notebooks == null || notebooks.length == 0) {
            return;
        }
        String sql = "update notebook set title=?,show_order=?,update_time=? where id =?";
        int size = notebooks.length;
        if (size == 1) {
            Notebook notebook = notebooks[0];
            update(conn, sql, notebook.getTitle(), notebook.getShowOrder(), notebook.getUpdateTime(), notebook.getId());
        } else {
            Object[][] args = new Object[size][4];
            for (int i = 0; i < size; i++) {
                Notebook notebook = notebooks[i];
                args[i][0] = notebook.getTitle();
                args[i][1] = notebook.getShowOrder();
                args[i][2] = notebook.getUpdateTime();
                args[i][3] = notebook.getId();
            }
            updateBatch(conn, sql, args);
        }
    }

    @Override
    public void update(Connection conn, Notebook notebook) {
        String sql = "update notebook set title=?,show_order=?,update_time=? where id =?";
        update(conn, sql, notebook.getTitle(), notebook.getShowOrder(), notebook.getUpdateTime(), notebook.getId());
    }

    @Override
    public Notebook findById(Connection conn, Integer id) {
        String sql = SELECT_SQL + " where id =? limit 1";
        //return query(conn,Customer.class, sql, id);
        return getBean(conn, sql, id);
    }

    @Override
    public void exchangeShowOrder(Connection conn, Integer showOrder1, Integer showOrder2) {
        String sql = "UPDATE notebook " +
                "SET show_order = (CASE WHEN show_order = ? THEN ? " +
                "                  WHEN show_order = ?  THEN ? END )" +
                "where show_order=? or show_order=?";
        update(conn, sql, showOrder1, showOrder2, showOrder2, showOrder1, showOrder1, showOrder2);
    }

    @Override
    public List<String> getTitles(Connection conn) {
        String sql = "select distinct title from notebook order by show_order asc;";
        return queryTitleList(conn, sql);
    }


    @Override
    public Notebook findByTitle(Connection conn, String title) {
        String sql = SELECT_SQL + "where title =? limit 1";
        return getBean(conn, sql, title);
    }

    @Override
    public List<Notebook> findAll(Connection conn) {
        String sql = SELECT_SQL + "order by show_order asc";
        return queryList(conn, sql);
    }

}
