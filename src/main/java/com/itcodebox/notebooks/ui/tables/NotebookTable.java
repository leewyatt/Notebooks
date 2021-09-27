package com.itcodebox.notebooks.ui.tables;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.SeparatorComponent;
import com.itcodebox.notebooks.entity.Notebook;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.impl.NoteServiceImpl;
import com.itcodebox.notebooks.service.impl.NotebookServiceImpl;
import com.itcodebox.notebooks.utils.CustomFileUtil;
import com.itcodebox.notebooks.utils.ExportUtil;
import icons.PluginIcons;

import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;
/**
 * @author LeeWyatt
 */
public class NotebookTable extends AbstractRecordTable<Notebook> {

    private final NotebookTableDragMoveHandler dragMoveHandler;
    private final NotebookTableDragOnTransferHandler dragOnHandler;

    public NotebookTable(Project project) {
        super(project);
        dragMoveHandler= new NotebookTableDragMoveHandler(project, this);
        dragOnHandler = new NotebookTableDragOnTransferHandler(project, this);
        setRecordDragMode(RecordDragMode.MOVE_UP_OR_DOWN);
    }

    @Override
    public void setRecordDragMode(RecordDragMode recordDragMode) {
        if (recordDragMode == RecordDragMode.MOVE_UP_OR_DOWN) {
            setTransferHandler(dragMoveHandler);
        }
        if(recordDragMode == RecordDragMode.DRAG_ON){
            setTransferHandler(dragOnHandler);
        }
    }


    @Override
    public JBPopupMenu createPopupMenu() {
        return new TablePopupMenu();
    }

    class TablePopupMenu extends JBPopupMenu {

        public TablePopupMenu() {
            NotebookTable notebookTable = NotebookTable.this;
            addRenameMenu(notebookTable);
            addDeleteMenu(notebookTable);
            add(new SeparatorComponent());
            addExportJsonMenu();
            addExportMDMenu();
        }

        private void addExportJsonMenu() {
            JBMenuItem menuItemJson = new JBMenuItem(message("mainPanel.action.exportJson.text"), AllIcons.ToolbarDecorator.Export);
            menuItemJson.addActionListener(e-> exportSingleJson());
            add(menuItemJson);
        }

        private void addDeleteMenu(NotebookTable notebookTable) {
            JBMenuItem menuItemDelete = new JBMenuItem(message("menu.item.delete"), PluginIcons.Delete);
            menuItemDelete.addActionListener(e -> {
                int result = Messages.showOkCancelDialog(
                        message("messagebox.deleteNotebook.message"),
                        message("messagebox.deleteNotebook.title"),
                        message("messagebox.ok.delete"),
                        message("messagebox.cancel"),
                        Messages.getWarningIcon());
                Notebook notebook = notebookTable.getSelectedObject();
                if (result == Messages.OK && notebook != null) {
                    // 准备工作: 获取相关图片
                    List<String> records = NoteServiceImpl.getInstance().getImageRecordsByNotebookId(notebook.getId());
                    //1. 数据库里删除
                    NotebookServiceImpl.getInstance().delete(notebook.getId());
                    //2. UI更新
                    ApplicationManager
                            .getApplication()
                            .getMessageBus()
                            .syncPublisher(RecordListener.TOPIC)
                            .onNotebookRemoved(project, notebook);
                    // 收尾工作: 删除相关图片
                    CustomFileUtil.deleteImages(records);
                }
            });
            add(menuItemDelete);
        }

        private void addExportMDMenu() {
            JBMenuItem menuItemExport = new JBMenuItem(message("menu.item.exportMarkdown"), PluginIcons.MarkdownFile);
            menuItemExport.addActionListener(e -> exportAction());
            add(menuItemExport);
        }



        private void addRenameMenu(NotebookTable notebookTable) {
            JBMenuItem menuItemRename = new JBMenuItem(message("menu.item.rename"), PluginIcons.Rename);
            menuItemRename.addActionListener(e -> notebookTable.editCellAt(notebookTable.getSelectedRow(), 0));
            add(menuItemRename);
        }
    }

    public void exportSingleJson() {
        Notebook notebook = getSelectedObject();
        if (notebook ==null) {
            return;
        }
        DateTimeFormatter fileTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String fileTimeStr = fileTimeFormatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault()));
        Path path = CustomFileUtil.choosePath(project,  notebook.getTitle()==null?"Notebook_Data":notebook.getTitle());
        if (path != null) {
            ExportUtil.exportJsonAndImage(project, path, fileTimeStr,notebook.getId());
        }
    }

    public void exportAction() {
        //情况1.  没有选中行 直接返回
        Notebook notebook = getSelectedObject();
        if (notebook == null || notebook.getId() == null) {
            return;
        }
        DateTimeFormatter fileTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String fileTimeStr = fileTimeFormatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault()));
        Path path = CustomFileUtil.choosePath(project, notebook.getTitle()==null?"Notebook_Markdown":notebook.getTitle());
        if (path != null) {
            ExportUtil. exportMarkdownFile(project,path,notebook,fileTimeStr);
        }
    }
}
