package com.itcodebox.notebooks.ui.tables;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.ui.TableSpeedSearch;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ListTableModel;
import com.itcodebox.notebooks.entity.Record;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * @author LeeWyatt
 */
public abstract class AbstractRecordTable<T extends Record> extends TableView<T> {

    //private final @NotNull Border FOCUS_LOST_BORDER = BorderFactory.createLineBorder(JBColor.WHITE);
    //private final @NotNull Border FOCUS_GAINED_BORDER = BorderFactory.createEtchedBorder();
    //private final @NotNull Border FOCUS_GAINED_BORDER = BorderFactory.createRaisedSoftBevelBorder();

    public enum RecordDragMode {
        /**
         * 上下移动位置
         */
        MOVE_UP_OR_DOWN,
        /**
         * 改变父级
         */
        DRAG_ON
    }

    protected RecordDragMode recordDragMode;

    /**
     * 右键菜单常见的功能编辑,删除...
     *
     * @return 右键点击后需要弹出的菜单
     */
    public abstract JBPopupMenu createPopupMenu();

    protected ListTableModel<T> recordModel;
    protected Project project;

    /**
     * tableSpeedSearch 搜索的好像就是toString里的内容,所以toString里返回的只是title的字符串
     */
    private TableSpeedSearch tableSpeedSearch;

    public AbstractRecordTable(Project project) {
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
        // 支持拖动交换位置
        //setTransferHandler(getRecordMoveTransferHandler());
        // 添加鼠标右键响应, 弹出菜单
        addMouseListener(mouseHandler);
    }


    public abstract void setRecordDragMode(RecordDragMode recordDragMode);

    private MouseAdapter mouseHandler = new MouseAdapter() {

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!AppSettingsState.getInstance().readOnlyMode && SwingUtilities.isRightMouseButton(e) && !e.isConsumed() && AbstractRecordTable.this.getSelectedObject() != null) {
                createPopupMenu().show(AbstractRecordTable.this, e.getX(), e.getY());
                e.consume();
            }
        }
    };

    public void setRecordModel(ListTableModel<T> recordModel) {
        this.recordModel = recordModel;
        setModelAndUpdateColumns(recordModel);
    }

    public ListTableModel<T> getRecordModel() {
        return recordModel;
    }

    public void addAndSelected(T t) {
        addAndSelected(t, false);
    }

    public void addAndSelected(T t, boolean editing) {
        addRow(t);
        int index = getRecordModel().indexOf(t);
        if (index == -1) {
            return;
        }
        scrollRectToVisible(getCellRect(index, 0, true));
        setRowSelectionInterval(index, index);
        if (editing) {
            editCellAt(index, 0);
        }
    }

    /**
     * 返回选择行的id
     */
    public Integer getSelectedRecordId() {
        T t = getSelectedObject();
        return t == null ? -1 : t.getId();
    }

    /**
     * 添加数据
     *
     * @param t 一行数据
     */
    public void addRow(T t) {
        recordModel.addRow(t);
    }

    public void addRow(List<T> list) {
        recordModel.addRows(list);
    }

    public void removeRow(T t) {
        if (t == null) {
            return;
        }
        int index = findIndexById(t.getId());
        if (index != -1) {
            recordModel.removeRow(index);
        }
    }

    /**
     * 选择某一行
     *
     * @param t 一行数据
     */
    public void selectedRow(T t) {
        if (t == null || t.getId() == null) {
            return;
        }
        int index = findIndexById(t.getId());
        //思考 如果不判断index为-1,会不会报错
        if (index != -1) {
            scrollRectToVisible(getCellRect(index, 0, true));
            setRowSelectionInterval(index, index);
        }
    }

    public void selectedRowByTitle(String title) {
        if (title == null ) {
            return;
        }
        int index = findIndexByTitle(title);
        //思考 如果不判断index为-1,会不会报错
        if (index != -1) {
            scrollRectToVisible(getCellRect(index, 0, true));
            setRowSelectionInterval(index, index);
        }
    }

    public void selectedRowById(Integer id) {
        if (id == null) {
            return;
        }
        int index = findIndexById(id);
        //思考 如果不判断index为-1,会不会报错
        if (index != -1) {
            scrollRectToVisible(getCellRect(index, 0, true));
            setRowSelectionInterval(index, index);
        }
    }

    /**
     * 编辑某一行
     *
     * @param t 指定数据所占的行
     */
    public void editRow(T t) {
        if (t == null || t.getId() == null) {
            return;
        }
        int index = findIndexById(t.getId());
        if (index != -1) {
            scrollRectToVisible(getCellRect(index, 0, true));
            setRowSelectionInterval(index, index);
            editCellAt(index, 0);
        }
    }

    /**
     * 滚动到某一行
     *
     * @param t 一行数据
     */
    public void scrollToRow(T t) {
        if (t == null || t.getId() == null) {
            return;
        }
        int index = findIndexById(t.getId());
        if (index != -1) {
            scrollRectToVisible(getCellRect(index, 0, true));
        }
    }

    public void clearRows() {
        int count = recordModel.getRowCount();
        for (int i = count - 1; i >= 0; i--) {
            recordModel.removeRow(i);
        }
    }

    /**
     * 通过ID 来查找是否包含某行
     */
    public boolean containRow(Integer id) {
        if (recordModel == null) {
            return false;
        }
        List<T> items = recordModel.getItems();
        for (T item : items) {
            if (id.equals(item.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通过id 来找到某行
     */
    public T findById(Integer id) {
        if (id == null || recordModel == null) {
            return null;
        }
        List<T> items = recordModel.getItems();
        for (T item : items) {
            if (id.equals(item.getId())) {
                return item;
            }
        }
        return null;
    }

    public int findIndexById(Integer id) {
        if (id == null || recordModel == null) {
            return -1;
        }
        List<T> items = recordModel.getItems();
        int size = items.size();
        for (int i = 0; i < size; i++) {
            if (id.equals(items.get(i).getId())) {
                return i;
            }
        }
        return -1;
    }

    public int findIndexByTitle(String  title) {
        if (title == null || recordModel == null) {
            return -1;
        }
        List<T> items = recordModel.getItems();
        int size = items.size();
        for (int i = 0; i < size; i++) {
            if (title.equals(items.get(i).getTitle())) {
                return i;
            }
        }
        return -1;
    }

    public void selectedFirst() {
        if (recordModel.getRowCount() > 0) {
            selectedRow(recordModel.getItem(0));
        }
    }
}
