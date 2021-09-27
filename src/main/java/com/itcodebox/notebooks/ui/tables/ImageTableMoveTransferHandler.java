package com.itcodebox.notebooks.ui.tables;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.ListTableModel;
import com.itcodebox.notebooks.entity.ImageRecord;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.projectservice.NotebooksUIManager;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.NoteService;
import com.itcodebox.notebooks.service.impl.NoteServiceImpl;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsState;
import com.itcodebox.notebooks.utils.ImageRecordUtil;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/**
 * 支持在表格上使用鼠标上下拖动来交换行的位置
 *
 * @author LeeWyatt
 */
public class ImageTableMoveTransferHandler extends TransferHandler {
    protected final DataFlavor localObjectFlavor;
    protected final ImageTable table;
    protected final Project project;
    protected final NotebooksUIManager uiManager;

    public ImageTableMoveTransferHandler(Project project, ImageTable table) {
        this.project = project;
        this.uiManager = project.getService(NotebooksUIManager.class);
        this.table = table;
        localObjectFlavor = new ActivationDataFlavor(
                ImageRecord.class, DataFlavor.javaJVMLocalObjectMimeType, ImageRecord.class.toString());
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
        return c == table ? TransferHandler.COPY_OR_MOVE : TransferHandler.NONE;
    }

    @Override
    public boolean importData(TransferSupport info) {
        ImageTable target = (ImageTable) info.getComponent();
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
            ListTableModel<ImageRecord> model = target.getListTableModel();
            ImageRecord data = (ImageRecord) info.getTransferable().getTransferData(localObjectFlavor);
            int rowFrom = model.indexOf(data);
            if (rowFrom != -1 && rowFrom != rowEnd) {
                if (rowEnd > rowFrom) {
                    rowEnd--;
                }
                if (rowEnd == rowFrom) {
                    return false;
                }
                model.removeRow(rowFrom);
                model.insertRow(rowEnd, data);

                updateOrderInDataBase(model);
                //最后来处理下选择状态的恢复
                target.getSelectionModel().addSelectionInterval(rowEnd, rowEnd);
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
     * 在数据库里更新显示顺序
     *
     */
    protected  void updateOrderInDataBase(ListTableModel<ImageRecord> tableModel){
        Note note = uiManager.getMainPanel().getNoteTable().getSelectedObject();
        if (note == null) {
            return;
        }
        String string = ImageRecordUtil.convertToString(tableModel.getItems());
        NoteService noteService = NoteServiceImpl.getInstance();
        note.setImageRecords(string);
        note.setUpdateTime(System.currentTimeMillis());
        noteService.update(note);
        //通知改变(其实UI上看不出来改变) 因为这里修改的就是showOrder
        ApplicationManager.getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onNoteUpdated(project,new Note[]{note});
    }

    @Override
    protected void exportDone(JComponent c, Transferable t, int act) {
        c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

}