package com.itcodebox.notebooks.ui.tables;

import com.intellij.openapi.project.Project;
import com.intellij.ui.TableSpeedSearch;
import com.intellij.ui.table.TableView;
import com.itcodebox.notebooks.entity.ImageRecord;

import javax.swing.*;
import java.awt.*;

/**
 * @author LeeWyatt
 */
public class ImageTable extends TableView<ImageRecord> {
    private Project project;
    private TableSpeedSearch tableSpeedSearch;
    public ImageTable(Project project) {
        this.project = project;
        // 单选
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // 设置选中行
        setRowSelectionAllowed(true);
        // 开启抗锯齿
        setEnableAntialiasing(true);
        // 如果数据太长,也不要超越表格进行显示
        setExpandableItemsEnabled(false);
        // 不显示表中间的间隔
        setShowGrid(false);
        // 单元格的间隙为0
        setIntercellSpacing(new Dimension(0, 0));
        //让TableView可以搜索
        tableSpeedSearch = new TableSpeedSearch(this);
        //让表格支持拖动
        setDragEnabled(true);
        // 设置表格拖动模式为插入行(实现位置的交换)
        setDropMode(DropMode.INSERT_ROWS);
        setTransferHandler(new ImageTableMoveTransferHandler(project, this));
    }

}
