package com.itcodebox.notebooks.ui.tables;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.Messages;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.NoteService;
import com.itcodebox.notebooks.service.impl.NoteServiceImpl;
import com.itcodebox.notebooks.ui.dialog.EditNoteDialog;
import com.itcodebox.notebooks.utils.CustomFileUtil;
import icons.PluginIcons;

import java.awt.datatransfer.StringSelection;
import java.util.List;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class NoteTable extends AbstractRecordTable<Note> {

    private final NoteTableDragMoveHandler dragMoveHandler;

    public NoteTable(Project project) {
        super(project);
        dragMoveHandler = new NoteTableDragMoveHandler(project, this);
        setRecordDragMode(RecordDragMode.MOVE_UP_OR_DOWN);
    }

    @Override
    public void setRecordDragMode(RecordDragMode recordDragMode) {
        setTransferHandler(dragMoveHandler);
    }

    @Override
    public JBPopupMenu createPopupMenu() {
        return new TablePopupMenu();
    }

    class TablePopupMenu extends JBPopupMenu {
        public TablePopupMenu() {
            NoteTable noteTable = NoteTable.this;
            // 1. 重命名
            JBMenuItem menuItemRename = new JBMenuItem(message("menu.item.rename"), PluginIcons.Rename);
            //menuItemRename.addActionListener(e -> noteTable.editCellAt(noteTable.getSelectedRow(), 0));
            menuItemRename.addActionListener(e -> noteTable.editCellAt(noteTable.getSelectedRow(),0));
            add(menuItemRename);
            //2. 复制内容
            JBMenuItem menuItemCopy = new JBMenuItem(message("menu.item.copyContent"), AllIcons.Actions.Copy);
            menuItemCopy.addActionListener(e -> {
                Note note = noteTable.getSelectedObject();
                if (note != null) {
                    String content = note.getContent();
                    PluginConstant.CLIPBOARD.setContents(new StringSelection(content == null || content.trim().isEmpty() ? "No data" : content), null);
                }
            });
            add(menuItemCopy);

            //3. 编辑
            JBMenuItem menuItemEdit = new JBMenuItem(message("menu.item.editNote"), AllIcons.Actions.Edit);
            menuItemEdit.addActionListener(e -> {
                Note note = noteTable.getSelectedObject();
                if (note != null) {
                    new EditNoteDialog(project, note).show();
                }
            });
            add(menuItemEdit);

            //4. 删除
            JBMenuItem menuItemDelete = new JBMenuItem(message("menu.item.delete"), PluginIcons.Delete);
            menuItemDelete.addActionListener(e -> {
                int result = Messages.showOkCancelDialog(message("messagebox.deleteNote.message"),
                        message("messagebox.deleteNote.title"),
                        message("messagebox.ok.delete"),
                        message("messagebox.cancel"),
                        Messages.getWarningIcon());
                Note note = getSelectedObject();
                if (result == Messages.OK && note != null) {
                    NoteService noteService = NoteServiceImpl.getInstance();
                    //准备工作: 获取相关图片
                    List<String> records = noteService.getImageRecordsByNoteId(note.getId());
                    //数据库里删除
                    noteService.delete(note.getId());
                    ApplicationManager
                            .getApplication()
                            .getMessageBus()
                            .syncPublisher(RecordListener.TOPIC)
                            .onNoteRemoved(project, note);
                   //收尾工作: 删除相关图片
                    CustomFileUtil.deleteImages(records);
                }
            });
            add(menuItemDelete);
        }
    }

}
