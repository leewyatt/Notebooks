package com.itcodebox.notebooks.ui.tables;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.Messages;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.impl.ChapterServiceImpl;
import com.itcodebox.notebooks.service.impl.NoteServiceImpl;
import com.itcodebox.notebooks.utils.CustomFileUtil;
import icons.PluginIcons;

import java.util.List;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;
/**
 * @author LeeWyatt
 */
public class ChapterTable extends AbstractRecordTable<Chapter> {

    private final ChapterTableDragMoveHandler dragMoveHandler;
    private final ChapterTableDragOnHandler dragOnHandler;

    public ChapterTable(Project project) {
        super(project);
        dragMoveHandler = new ChapterTableDragMoveHandler(project, this);
        dragOnHandler = new ChapterTableDragOnHandler(project, this);
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
            ChapterTable chapterTable = ChapterTable.this;
            JBMenuItem menuItemRename = new JBMenuItem(message("menu.item.rename")+"           ", PluginIcons.Rename);
            menuItemRename.addActionListener(e -> chapterTable.editCellAt(chapterTable.getSelectedRow(), 0));
            add(menuItemRename);

            JBMenuItem menuItemDelete = new JBMenuItem(message("menu.item.delete"), PluginIcons.Delete);
            menuItemDelete.addActionListener(e -> {
                int result = Messages.showOkCancelDialog(
                        message("messagebox.deleteChapter.message"),
                        message("messagebox.deleteChapter.title"),
                        message("messagebox.ok.delete"),
                        message("messagebox.cancel"),
                        Messages.getWarningIcon());
                Chapter chapter = chapterTable.getSelectedObject();
                if (result == Messages.OK && chapter != null) {
                    // 准备工作: 获取相关图片
                    List<String> records = NoteServiceImpl.getInstance().getImageRecordsByChapterId(chapter.getId());
                    //一. 从数据库删除
                    ChapterServiceImpl.getInstance().delete(chapter.getId());
                    //二. 通知UI删除
                    ApplicationManager
                            .getApplication()
                            .getMessageBus()
                            .syncPublisher(RecordListener.TOPIC)
                            .onChapterRemoved(project, chapter);
                    // 收尾工作: 删除相关图片
                    CustomFileUtil.deleteImages(records);
                }
            });
            add(menuItemDelete);
        }
    }
}
