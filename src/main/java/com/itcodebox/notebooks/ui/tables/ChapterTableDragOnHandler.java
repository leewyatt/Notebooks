package com.itcodebox.notebooks.ui.tables;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.projectservice.NotebooksUIManager;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.NoteService;
import com.itcodebox.notebooks.service.impl.NoteServiceImpl;
import com.itcodebox.notebooks.ui.panes.MainPanel;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsState;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * 支持在表格上使用鼠标上下拖动来交换行的位置
 *
 * @author LeeWyatt
 */
public class ChapterTableDragOnHandler extends TransferHandler {
    private final DataFlavor localObjectFlavor;
    private final ChapterTable table;
    private final Project project;
    private final NotebooksUIManager uiManager;

    public ChapterTableDragOnHandler(Project project, ChapterTable table) {
        this.project = project;
        uiManager = project.getService(NotebooksUIManager.class);
        this.table = table;
        localObjectFlavor = new ActivationDataFlavor(
                Note.class, DataFlavor.javaJVMLocalObjectMimeType, Note.class.toString());
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        assert (c == table);
        return new DataHandler(table.getSelectedObject(), localObjectFlavor.getMimeType());
    }

    @Override
    public boolean canImport(TransferSupport info) {
        //table.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
        if (info == null|| AppSettingsState.getInstance().readOnlyMode) {
            return false;
        }
        return info.getComponent() == table && info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }
    


    @Override
    public boolean importData(TransferSupport info) {
        ChapterTable target = (ChapterTable) info.getComponent();
        JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
        int rowEnd = dl.getRow();
        int max = table.getModel().getRowCount();
        if (rowEnd < 0 || rowEnd > max) {
            rowEnd = max;
        }
        Chapter chapter = target.getRow(rowEnd);
        target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        try {
            Note note = (Note) info.getTransferable().getTransferData(localObjectFlavor);
            if (note.getChapterId().equals(chapter.getId())) {
                return false;
            }

            NoteService noteService = NoteServiceImpl.getInstance();
            //Note 如果重名的处理方式
            Note noteTemp = noteService.findByTitle(note.getTitle(), chapter.getId());
            if (noteTemp != null) {
                int result = Messages.showYesNoCancelDialog(
                        message("chapterTable.DragOn.titleConflict.msg"),
                        message("chapterTable.DragOn.titleConflict.title"),
                       message("chapterTable.DragOn.titleConflict.yes"),
                        message("chapterTable.DragOn.titleConflict.no"),
                        message("button.cancel"),
                        Messages.getQuestionIcon());
                //替换操作
                if (result == Messages.YES) {
                    //1. 删除目标章节的note
                    noteService.delete(noteTemp.getId());
                    //2. 删除后的UI更新
                    ApplicationManager.getApplication()
                            .getMessageBus()
                            .syncPublisher(RecordListener.TOPIC)
                            .onNoteRemoved(project,noteTemp);
                    //3. 移动当前Note到新的章节
                    moveToNewChapter(chapter, note, noteService);
                    return true;
                }else if (result==Messages.NO){
                    uiManager.getMainPanel().getNoteTable().editRow(note);
                }
                return false;
            }else{
                moveToNewChapter(chapter, note, noteService);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void moveToNewChapter(Chapter chapter, Note note, NoteService noteService) {
        noteService.delete(note.getId());
        //1. 删除
        ApplicationManager.getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onNoteRemoved(project, note);
        //2. 修改
        note.setChapterId(chapter.getId());
        Note newNote = noteService.insert(note);
        //3. 添加
        ApplicationManager.getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onNoteAdd(project, newNote);
    }

    @Override
    protected void exportDone(JComponent c, Transferable t, int act) {
        MainPanel mainPanel = uiManager.getMainPanel();
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