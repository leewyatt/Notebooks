package com.itcodebox.notebooks.ui.tables;

import com.intellij.openapi.project.Project;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ListTableModel;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.entity.Record;
import com.itcodebox.notebooks.projectservice.NotebooksUIManager;
import com.itcodebox.notebooks.ui.panes.MainPanel;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsState;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.lang.reflect.Array;

/**
 * 支持在表格上使用鼠标上下拖动来交换行的位置
 *
 * @author LeeWyatt
 */
@SuppressWarnings("unchecked")
public abstract class AbstractTableMoveTransferHandler<T extends Record> extends TransferHandler {
    protected final DataFlavor localObjectFlavor;
    protected final AbstractRecordTable<T> table;
    protected final Class<T> clazz;
    protected final Project project;
    protected final NotebooksUIManager uiManger;

    public AbstractTableMoveTransferHandler(Project project, AbstractRecordTable<T> table, Class<T> clazz) {
        this.project = project;
        this.uiManger = project.getService(NotebooksUIManager.class);
        this.table = table;
        this.clazz = clazz;
        localObjectFlavor = new ActivationDataFlavor(
                clazz, DataFlavor.javaJVMLocalObjectMimeType, clazz.toString());
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        assert (c == table);
        return new DataHandler(table.getSelectedObject(), localObjectFlavor.getMimeType());
    }

    @Override
    public boolean canImport(TransferSupport info) {
        //table.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop)
        if (info == null || AppSettingsState.getInstance().readOnlyMode) {
            return false;
        }
        return info.getComponent() == table && info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
    }

    /**
     * 如果拖动从下上拖动,变成了从1个table,拖动到了一个父级table的item上,那么会触发改变,把当前的item更换一个父级item
     *
     * @return 拖动的处理
     */
    @Override
    public int getSourceActions(JComponent c) {
        MainPanel mainPanel = uiManger.getMainPanel();
        ChapterTable chapterTable = mainPanel.getChapterTable();
        NotebookTable notebookTable = mainPanel.getNotebookTable();
        NoteTable noteTable = mainPanel.getNoteTable();
        if (c == chapterTable) {
            notebookTable.setDropMode(DropMode.ON);
            notebookTable.setRecordDragMode(AbstractRecordTable.RecordDragMode.DRAG_ON);
            return TransferHandler.COPY_OR_MOVE;
        } else if (c == noteTable) {
            chapterTable.setDropMode(DropMode.ON);
            chapterTable.setRecordDragMode(AbstractRecordTable.RecordDragMode.DRAG_ON);
            return TransferHandler.COPY_OR_MOVE;
        }
        return TransferHandler.COPY_OR_MOVE;
    }



    @Override
    public boolean importData(TransferSupport info) {
        TableView<T> target = (TableView<T>) info.getComponent();
        //下面的代码 偶尔会抛异常  IllegalStateException: Not a drop; 所以判断下
        JTable.DropLocation dl;
        try {
            dl = (JTable.DropLocation) info.getDropLocation();
        } catch (IllegalStateException exception) {
            return false;
        }
        int rowEnd = dl.getRow();
        int max = table.getModel().getRowCount();
        if (rowEnd < 0 || rowEnd > max) {
            rowEnd = max;
        }
        target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        try {
            ListTableModel<T> model = target.getListTableModel();
            T data = (T) info.getTransferable().getTransferData(localObjectFlavor);
            int rowFrom = model.indexOf(data);
            if (rowFrom != -1 && rowFrom != rowEnd) {
                if (rowEnd > rowFrom) {
                    rowEnd--;
                }
                if (rowEnd == rowFrom) {
                    return false;
                }
                // 上下移动位置, 需要全局通知其他工程更新界面

                MainPanel mainPanel = uiManger.getMainPanel();
                Chapter selectedChapter = mainPanel.getChapterTable().getSelectedObject();
                Note selectedNote = mainPanel.getNoteTable().getSelectedObject();

                model.removeRow(rowFrom);
                model.insertRow(rowEnd, data);

                // 其余牵扯的行. 也需要通知其他工程和数据库修改ShowOrder
                T[] records;
                //从上往下拖
                if (rowEnd > rowFrom) {
                    T temp = table.getRow(rowEnd);
                    int firstShowOrder = temp.getShowOrder();
                    for (int i = rowEnd; i > rowFrom; i--) {
                        T nLastOne = table.getRow(i);
                        T nPrevious = table.getRow(i - 1);
                        nLastOne.setShowOrder(nPrevious.getShowOrder());
                    }
                    table.getRow(rowFrom).setShowOrder(firstShowOrder);
                    records = (T[]) Array.newInstance(clazz, rowEnd - rowFrom + 1);
                    for (int i = rowFrom; i <= rowEnd; i++) {
                        records[i - rowFrom] = table.getRow(i);
                    }
                } else {//从下往下拖
                    T temp = table.getRow(rowEnd);
                    int lastShowOrder = temp.getShowOrder();
                    for (int i = rowEnd; i < rowFrom; i++) {
                        T n1 = table.getRow(i);
                        T n2 = table.getRow(i + 1);
                        n1.setShowOrder(n2.getShowOrder());
                    }
                    table.getRow(rowFrom).setShowOrder(lastShowOrder);
                    records = (T[]) Array.newInstance(clazz, rowFrom - rowEnd + 1);
                    for (int i = rowEnd; i <= rowFrom; i++) {
                        records[i - rowEnd] = table.getRow(i);
                    }
                }
                updateOrderInDataBase(records);
                updateOrderInTable(rowFrom, rowEnd, data);
                //最后来处理下选择状态的恢复
                target.getSelectionModel().addSelectionInterval(rowEnd, rowEnd);
                mainPanel.getChapterTable().selectedRow(selectedChapter);
                mainPanel.getNoteTable().selectedRow(selectedNote);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 在(其他打开的工程里)的表格中更新行的位置; 因为触发拖动的工程里,. 已经修改了行的位置
     *
     * @param rowFrom 开始拖动的位置
     * @param rowEnd  结束拖动的位置
     * @param data    被拖动的数据
     */
    protected abstract void updateOrderInTable(int rowFrom, int rowEnd, Object data);

    /**
     * 在数据库里更新显示顺序
     *
     * @param ary 需要更新的对象
     */
    protected abstract void updateOrderInDataBase(T[] ary);

    @Override
    protected void exportDone(JComponent c, Transferable t, int act) {
        MainPanel mainPanel = uiManger.getMainPanel();
        ChapterTable chapterTable = mainPanel.getChapterTable();
        chapterTable.setDropMode(DropMode.INSERT_ROWS);
        chapterTable.setRecordDragMode(AbstractRecordTable.RecordDragMode.MOVE_UP_OR_DOWN);
        NotebookTable notebookTable = mainPanel.getNotebookTable();
        notebookTable.setDropMode(DropMode.INSERT_ROWS);
        notebookTable.setRecordDragMode(AbstractRecordTable.RecordDragMode.MOVE_UP_OR_DOWN);
        NoteTable noteTable = mainPanel.getNoteTable();
        noteTable.setDropMode(DropMode.INSERT_ROWS);
        notebookTable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        chapterTable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        noteTable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

}