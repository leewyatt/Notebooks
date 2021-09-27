package com.itcodebox.notebooks.service;

import com.itcodebox.notebooks.constant.PluginConstant;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 需要两个依赖jar
 * commons-dbcp.jar
 * commons-pool.jar
 *
 * @author LeeWyatt
 */
public class DatabaseBasicService {
    private  static final String DATABASE_DRIVER = "org.sqlite.JDBC";
    private  static final String DATABASE_URL = "jdbc:sqlite:" + PluginConstant.DB_FILE_PATH;
    private static final String EDITOR_OFFSET_START = "offset_start";
    private static final String EDITOR_OFFSET_END = "offset_end";
    private static final String IMAGE_RECORDS = "image_records";
    private  BasicDataSource source;

    public DatabaseBasicService() {
        try {
            //创建了DBCP的数据库连接池
            source = new BasicDataSource();
            //设置基本信息
            source.setMaxActive(1);
            source.setDriverClassName(DATABASE_DRIVER);
            source.setUrl(DATABASE_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 如果不存在,创建DB文件
        createFileAndDir();
        // 如果表不存在,创建表
        initTable();
    }


    public  BasicDataSource getSource() {
        return source;
    }

    public  Connection getConnection() throws SQLException {
        return source.getConnection();
    }

    /**
     * 释放资源
     */
    public  void closeResource(Connection conn, Statement statement, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 如果不存在目录和文件就创建
     */
    private  void createFileAndDir() {
        //"C:\Users\Administrator\.ideaNotebooksFile"
        if (!Files.exists(PluginConstant.PROJECT_DB_DIRECTORY_PATH)) {
            try {
                Files.createDirectories(PluginConstant.PROJECT_DB_DIRECTORY_PATH);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //"C:\Users\Administrator\.ideaNotebooksFile\notebooks.db"
        if (!Files.exists(PluginConstant.DB_FILE_PATH)) {
            try {
                Files.createFile(PluginConstant.DB_FILE_PATH);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //创建图片文件夹.
        if (!Files.exists(PluginConstant.IMAGE_DIRECTORY_PATH)) {
            try {
                Files.createDirectories(PluginConstant.IMAGE_DIRECTORY_PATH);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //创建图片临时文件夹.
        if (!Files.exists(PluginConstant.TEMP_IMAGE_DIRECTORY_PATH)) {
            try {
                Files.createDirectories(PluginConstant.TEMP_IMAGE_DIRECTORY_PATH);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 如果不存在就初始化表
     */
    private  void initTable() {
        String createNotebookSQL = "CREATE TABLE IF NOT EXISTS notebook (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT," +
                "show_order INTEGER," +
                "create_time INTEGER," +
                "update_time INTEGER" +
                ")";


        String createChapterSQL = "CREATE TABLE IF NOT EXISTS chapter (" +
                "id INTEGER  PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "notebook_id INTEGER," +
                "show_order INTEGER," +
                "create_time INTEGER," +
                "update_time INTEGER" +
                ")";


        String createNoteSQL = "CREATE TABLE IF NOT EXISTS note (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "description TEXT," +
                "chapter_id INTEGER," +
                "notebook_id INTEGER," +
                "show_order INTEGER," +
                "content TEXT," +
                "source TEXT," +
                EDITOR_OFFSET_START+" INTEGER," +
                EDITOR_OFFSET_END+" INTEGER," +
                IMAGE_RECORDS+" TEXT,"+
                "type TEXT," +
                "create_time INTEGER," +
                "update_time INTEGER" +
                ")";


        //注意, SQLite 似乎不支持一次添加多个字段; 所以分开添加
        String addColOffsetStart = "ALTER TABLE note ADD COLUMN "+EDITOR_OFFSET_START+" Integer DEFAULT 0;";
        String addColOffsetEnd = "ALTER TABLE note ADD COLUMN "+EDITOR_OFFSET_END+" Integer DEFAULT 0;";
        //注意, 再次增加一个列,记录图片信息
        String addColImageRecords = "ALTER TABLE note ADD COLUMN "+IMAGE_RECORDS+" TEXT;";
        try {
            QueryRunner queryRunner = new QueryRunner(getSource());
            queryRunner.update(createNotebookSQL);
            queryRunner.update(createChapterSQL);
            queryRunner.update(createNoteSQL);
            boolean offsetColExists = isColumnExists("note", EDITOR_OFFSET_START, queryRunner);
            //如果不存在该字段 , 那么升级数据库,添加该字段;
            if (!offsetColExists) {
                queryRunner.update(addColOffsetStart);
                queryRunner.update(addColOffsetEnd);
            }
            boolean imageRecordsColExists = isColumnExists("note", IMAGE_RECORDS, queryRunner);
            if (!imageRecordsColExists) {
                queryRunner.update(addColImageRecords);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * 判断是否存在某个字段的方法
     * @param table 表
     * @param column 字段/列
     * @param queryRunner 查询
     * @return true 存在该字段 false 不存在该字段
     * @throws SQLException sql异常
     */
    private boolean isColumnExists (String table, String column,QueryRunner queryRunner ) throws SQLException {
        boolean isExists = false;
        List<String> name = queryRunner.query("PRAGMA table_info("+table+")", new ColumnListHandler<String>("name"));
        for (String s : name) {
            if (column.equalsIgnoreCase(s)) {
                isExists = true;
                break;
            }
        }
        return isExists;
    }
}
