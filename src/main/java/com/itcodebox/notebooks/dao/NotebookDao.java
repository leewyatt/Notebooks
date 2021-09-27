package com.itcodebox.notebooks.dao;

import com.itcodebox.notebooks.entity.Notebook;

import java.sql.Connection;
import java.util.List;

/**
 * @author LeeWyatt
 */
public interface NotebookDao extends CommonDao<Notebook> {

    Notebook findByTitle(Connection conn, String name);

    List<Notebook> findAll(Connection connection);

    List<String> getTitles(Connection conn);



}
