package com.itcodebox.notebooks.dao.impl;

import com.itcodebox.notebooks.dao.BaseDAO;
import com.itcodebox.notebooks.dao.SearchRecordDao;
import com.itcodebox.notebooks.entity.SearchMode;
import com.itcodebox.notebooks.entity.SearchRecord;
import com.itcodebox.notebooks.utils.StringUtil;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author LeeWyatt
 */
public class SearchRecordDaoImpl extends BaseDAO<SearchRecord> implements SearchRecordDao {
    private static SearchRecordDaoImpl instance;

    String sqlSearchNote ="SELECT note.type,note.content,note.description,note.id AS note_id,note.title AS note_title,note.chapter_id AS chapter_id,chapter.title AS chapter_title,note.notebook_id AS notebook_id,notebook.title AS notebook_title FROM note  " +
            "INNER JOIN chapter ON note.chapter_id=chapter.id  INNER JOIN notebook ON notebook.id=note.notebook_id where ";

    String sqlSearchChapter = "SELECT chapter.id AS chapter_id,chapter.title AS chapter_title,notebook_id ,notebook.title AS notebook_title FROM chapter " +
            "INNER JOIN notebook ON chapter.notebook_id=notebook.id where ";

    String sqlSearchNotebook ="SELECT id AS notebook_id, title AS notebook_title FROM notebook where ";


    public static synchronized SearchRecordDaoImpl getInstance() {
        if (instance == null) {
            instance = new SearchRecordDaoImpl();
        }
        return instance;
    }

    @Override
    public List<SearchRecord> searchKeywords(Connection conn, String keywords, SearchMode searchMode) {
        String[] kws = StringUtil.splitKeywords(keywords);
        if (SearchMode.Note == searchMode) {
            return  getRecordList(conn, kws, sqlSearchNote, "note.title like ? ", 1);
        } else if (SearchMode.NoteAndContent == searchMode) {
            return  getRecordList(conn, kws, sqlSearchNote, "note.title like ? or note.content like ? ", 2);
        } else if (SearchMode.NoteAndDescription == searchMode) {
            return getRecordList(conn, kws, sqlSearchNote, "note.title like ? or note.description like ? ", 2);
        } else if (SearchMode.NoteAndContentAndDescription == searchMode) {
            return getRecordList(conn, kws, sqlSearchNote, "note.title like ? or note.content like ? or note.description like ? ", 3);
        } else if (SearchMode.Chapter == searchMode) {
            return getRecordList(conn, kws, sqlSearchChapter, "chapter.title like ? ", 1);
        } else if (SearchMode.Notebook == searchMode) {
            return getRecordList(conn, kws, sqlSearchNotebook, "title like ? ", 1);
        } else if (SearchMode.All == searchMode) {
            List<SearchRecord> notebookList = getRecordList(conn, kws, sqlSearchNotebook, "title like ? ", 1);
            notebookList.addAll(getRecordList(conn, kws, sqlSearchChapter, "chapter.title like ? ", 1));
            notebookList.addAll(getRecordList(conn, kws, sqlSearchNote, "note.title like ? ", 1));
            notebookList.sort(Comparator.comparing(SearchRecord::toString));
            return notebookList;
        } else if (SearchMode.AllAndContent == searchMode) {
            List<SearchRecord> notebookList = getRecordList(conn, kws, sqlSearchNotebook, "title like ? ", 1);
            notebookList.addAll(getRecordList(conn, kws, sqlSearchChapter, "chapter.title like ? ", 1));
            notebookList.addAll(getRecordList(conn, kws, sqlSearchNote, "note.title like ? or note.content like ? ", 2));
            notebookList.sort(Comparator.comparing(SearchRecord::toString));
            return notebookList;
        }else if (SearchMode.AllAndDescription == searchMode) {
            List<SearchRecord> notebookList = getRecordList(conn, kws, sqlSearchNotebook, "title like ? ", 1);
            notebookList.addAll(getRecordList(conn, kws, sqlSearchChapter, "chapter.title like ? ", 1));
            notebookList.addAll(getRecordList(conn, kws, sqlSearchNote, "note.title like ? or note.description like ? ", 2));
            notebookList.sort(Comparator.comparing(SearchRecord::toString));
            return notebookList;
        }else if (SearchMode.AllAndContentAndDescription == searchMode) {
            List<SearchRecord> notebookList = getRecordList(conn, kws, sqlSearchNotebook, "title like ? ", 1);
            notebookList.addAll(getRecordList(conn, kws, sqlSearchChapter, "chapter.title like ? ", 1));
            notebookList.addAll(getRecordList(conn, kws, sqlSearchNote, "note.title like ? or note.content like ? or note.description like ? ", 3));
            notebookList.sort(Comparator.comparing(SearchRecord::toString));
            return notebookList;
        }
        return new ArrayList<SearchRecord>();
    }



    private List<SearchRecord> getRecordList(Connection conn, String[] kws, String sqlSearNote, String s,int times) {
        if (kws == null) {
            return new ArrayList<SearchRecord>();
        }
        StringBuilder builder = new StringBuilder(512);
        builder.append(sqlSearNote);
        int len = kws.length;
        Object[] objs = new Object[len*times];

        for (int i = 0; i < len; i++) {
            // 注意:如果是在完整的SQL语句里, 应该是 '%keywords%'
            // 当作为参数传递进去时 , 不需要外层的单独引号 %keywords%
            for (int j = 0; j < times; j++) {
                objs[i * times+j] = "%" + kws[i] + "%";
            }
            builder.append(s);
            if (i != len - 1) {
                builder.append(" or ");
            }
        }
        //注意: 需要在sql语句里写下转义字符用的 转义符号是什么
        builder.append("escape '/'");
        return queryList(conn, builder.toString(), objs);
    }
}
