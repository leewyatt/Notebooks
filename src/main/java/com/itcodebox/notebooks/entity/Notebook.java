/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itcodebox.notebooks.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

/**
 *
 * @author Administrator
 */
public class Notebook extends Record{

    public Notebook() {
    }

    public Notebook(String title,Long createTime) {
        this(title, createTime, createTime);
    }

    public Notebook(String title, Long createTime, Long updateTime) {
        this.title = title;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Notebook(String jsonStr) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Notebook notebook = objectMapper.readValue(jsonStr, Notebook.class);
            this.id = notebook.getId();
            this.createTime = notebook.getCreateTime();
            this.title = notebook.getTitle();
            this.updateTime = notebook.getUpdateTime();
            this.showOrder = notebook.getShowOrder();;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Record record = (Record) o;
        return Objects.equals(id, record.id) && Objects.equals(title, record.title) && Objects.equals(showOrder, record.showOrder) && Objects.equals(createTime, record.createTime) && Objects.equals(updateTime, record.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, showOrder, createTime, updateTime);
    }


}
