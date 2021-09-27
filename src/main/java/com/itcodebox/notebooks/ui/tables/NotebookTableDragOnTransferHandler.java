package com.itcodebox.notebooks.ui.tables;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.entity.Notebook;
import com.itcodebox.notebooks.projectservice.NotebooksUIManager;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.ChapterService;
import com.itcodebox.notebooks.service.NoteService;
import com.itcodebox.notebooks.service.NotebookService;
import com.itcodebox.notebooks.service.impl.ChapterServiceImpl;
import com.itcodebox.notebooks.service.impl.NoteServiceImpl;
import com.itcodebox.notebooks.service.impl.NotebookServiceImpl;
import com.itcodebox.notebooks.ui.panes.MainPanel;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsState;
import org.jetbrains.annotations.NotNull;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * 支持在表格上使用鼠标上下拖动来交换行的位置
 *
 * @author LeeWyatt
 */
public class NotebookTableDragOnTransferHandler extends TransferHandler {
    private final DataFlavor localObjectFlavor;
    private final NotebookTable table;
    private final Project project;
    private final NotebooksUIManager uiManager;

    public NotebookTableDragOnTransferHandler(@NotNull Project project, NotebookTable table) {
        this.project = project;
        uiManager = project.getService(NotebooksUIManager.class);
        this.table = table;
        localObjectFlavor = new ActivationDataFlavor(
                Chapter.class, DataFlavor.javaJVMLocalObjectMimeType, Chapter.class.toString());
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        assert (c == table);
        return new DataHandler(table.getSelectedObject(), localObjectFlavor.getMimeType());
    }

    @Override
    public boolean canImport(TransferSupport info) {
        if (info == null || AppSettingsState.getInstance().readOnlyMode) {
            return false;
        }
        //鼠标样式的设置  table.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop)
        return info.getComponent() == table && info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferSupport info) {
        if (info == null) {
            return false;
        }
        NotebookTable target = (NotebookTable) info.getComponent();
        JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
        int rowEnd = dl.getRow();
        int max = table.getModel().getRowCount();
        if (rowEnd < 0 || rowEnd > max) {
            rowEnd = max;
        }
        Notebook targetNotebook = target.getRow(rowEnd);
        if (targetNotebook == null) {
            return false;
        }
        target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        try {
            Chapter sourceChapter = (Chapter) info.getTransferable().getTransferData(localObjectFlavor);
            if (sourceChapter.getNotebookId().equals(targetNotebook.getId())) {
                return true;
            }
            ChapterServiceImpl chapterService = ChapterServiceImpl.getInstance();
            Chapter targetChapter = chapterService.findByTitle(sourceChapter.getTitle(), targetNotebook.getId());
            //情况1: 目标book已经存在同名的Chapter

            if (targetChapter != null) {
                int result = Messages.showYesNoCancelDialog(
                        message("messagebox.nameConflict.message"),
                        message("messagebox.nameConflict.title"),
                        message("messagebox.nameConflict.yes"),
                        message("messagebox.nameConflict.no"),
                        message("messagebox.nameConflict.cancel"),
                        Messages.getQuestionIcon());
                if (result == Messages.YES) {
                    // 同名时处理方法1: 合并两个章节的内容
                    merge(sourceChapter, chapterService, targetChapter);
                } else if (result == Messages.NO) {
                    // 同名时处理方法2: 提示用户先改名
                    changTitle(sourceChapter);
                }

            } else {//情况2: 目标book没有同名的Chapter
                moveToNotebook(targetNotebook, sourceChapter, chapterService);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void changTitle(Chapter sourceChapter) {
        MainPanel mainPanel = project.getService(NotebooksUIManager.class).getMainPanel();
        if (mainPanel != null) {
            mainPanel.getChapterTable().editRow(sourceChapter);
        }
    }

    private static final int OVERWRITE = 0;
    private static final int SKIP = 1;
    private static final int OVERWRITE_ALL = 2;
    private static final int SKIP_ALL = 3;
    private static final int STOP = 4;
    private static final int ASK = 5;
    private static final int CLOSE = -1;

    private void merge(@NotNull Chapter sourceChapter, @NotNull ChapterServiceImpl chapterService, Chapter targetChapter) {
        //合并
        NoteService noteService = NoteServiceImpl.getInstance();
        List<Note> noteList = noteService.findAllByChapterId(sourceChapter.getId());

        // 下次循环是否询问同名时的冲突操作
        int batchChoose = ASK;
        //是否有残留,有跳过或者全部跳过的Note
        boolean hasLeftOver = false;
        for (Note note : noteList) {
            Note noteTemp = noteService.findByTitle(note.getTitle(), targetChapter.getId());
            if (noteTemp != null) {
                if (batchChoose == ASK) {
                    int result = Messages.showDialog(
                            "<html><body>" +
                                    message("notebookTable.merge.msg1")+"<br>" +
                                    "<b>"+message("notebookTable.merge.msg2")+"</b>" + getPath(note) + "<br/>" +
                                    "<b>"+message("notebookTable.merge.msg3")+"</b>" + getPath(noteTemp) + "<br/>" +
                                    "</html></body>"
                            , message("notebookTable.merge.title"),
                            new String[]{
                                    //0
                                    message("notebookTable.merge.choose0"),
                                    //1
                                    message("notebookTable.merge.choose1"),
                                    //2
                                    message("notebookTable.merge.choose2"),
                                    //3
                                    message("notebookTable.merge.choose3"),
                                    //4
                                    message("notebookTable.merge.choose4"),
                            }, 0, Messages.getWarningIcon());

                    //如果关闭了窗口, 没有进行任何操作
                    if (result == CLOSE || result == STOP) {
                        return;
                    } else if (result == OVERWRITE || result == OVERWRITE_ALL) {
                        // 选择了替换操作
                        if (result == OVERWRITE_ALL) {
                            batchChoose = OVERWRITE_ALL;
                        }
                        //1. 删除目标章节的note
                        noteService.delete(noteTemp.getId());
                        //2. 删除后的UI更新
                        ApplicationManager.getApplication()
                                .getMessageBus()
                                .syncPublisher(RecordListener.TOPIC)
                                .onNoteRemoved(project, noteTemp);
                        //3. 移动当前Note到新的章节
                        moveToNewChapter(targetChapter, note, noteService);
                    } else if (result == SKIP_ALL) {
                        batchChoose = SKIP_ALL;
                        hasLeftOver = true;
                    } else {
                        hasLeftOver = true;
                    }
                } else if (batchChoose == OVERWRITE_ALL) {
                    //1. 删除目标章节的note
                    noteService.delete(noteTemp.getId());
                    //2. 删除后的UI更新
                    ApplicationManager.getApplication()
                            .getMessageBus()
                            .syncPublisher(RecordListener.TOPIC)
                            .onNoteRemoved(project, noteTemp);
                    //3. 移动当前Note到新的章节
                    moveToNewChapter(targetChapter, note, noteService);
                }

            } else {
                moveToNewChapter(targetChapter, note, noteService);
            }

        }
        //如果全部移动,没有残留. 那么需要删除章节
        if (!hasLeftOver) {
            chapterService.delete(sourceChapter.getId());
            ApplicationManager.getApplication()
                    .getMessageBus()
                    .syncPublisher(RecordListener.TOPIC)
                    .onChapterRemoved(project, sourceChapter);
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

    private void moveToNotebook(@NotNull Notebook notebook, @NotNull Chapter sourceChapter, @NotNull ChapterServiceImpl chapterService) {
        //1. 查找该Chapter下的全部Note
        NoteService noteService = NoteServiceImpl.getInstance();
        List<Note> noteList = noteService.findAllByChapterId(sourceChapter.getId());
        //2. 从数据库和视图中 删除该Chapter
        chapterService.delete(sourceChapter.getId());
        ApplicationManager
                .getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onChapterRemoved(project, sourceChapter);
        //3. 修改chapter的bookid
        sourceChapter.setNotebookId(notebook.getId());
        //4. 插入更新后的chapter
        Chapter targetChapter = chapterService.insert(sourceChapter);
        ApplicationManager
                .getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onChapterAdd(project, targetChapter, false, false);

        //5. 更新Note的 从属关系
        for (Note note : noteList) {
            note.setNotebookId(notebook.getId());
            note.setChapterId(targetChapter.getId());
        }
        Note[] noteAry = new Note[noteList.size()];
        //6. 向数据库插入新的notes
        noteService.insert(noteList.toArray(noteAry));
        ApplicationManager.getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onNoteAdd(project, noteList);
    }

    private String getPath(Note note) {
        if (note == null) {
            return "";
        }
        String path = "";
        NotebookService notebookService = NotebookServiceImpl.getInstance();
        Notebook notebook = notebookService.findById(note.getNotebookId());
        if (notebook != null) {
            path += notebook.getTitle();
        }
        ChapterService chapterService = ChapterServiceImpl.getInstance();
        Chapter chapter = chapterService.findById(note.getChapterId());
        if (chapter != null) {
            path += ">" + chapter.getTitle();
        }
        path += ">" + note.getTitle();
        return path;
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