package com.itcodebox.notebooks.ui.tables;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.ChapterService;
import com.itcodebox.notebooks.service.impl.ChapterServiceImpl;

/**
 * @author LeeWyatt
 */
public class ChapterTableDragMoveHandler extends AbstractTableMoveTransferHandler<Chapter> {

    public ChapterTableDragMoveHandler(Project project, ChapterTable table) {
        super(project, table, Chapter.class);
    }

    @Override
    protected void updateOrderInDataBase(Chapter[] ary) {
        ChapterService chapterService = ChapterServiceImpl.getInstance();
        chapterService.update(ary);
        //通知改变(其实UI上看不出来改变) 因为这里修改的就是showOrder
        ApplicationManager.getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onChapterUpdated(project,ary);
    }


    @Override
    protected void updateOrderInTable(int rowFrom, int rowEnd, Object data) {
        //
        ApplicationManager.getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onChapterDragMove(project,(Chapter)data,rowFrom,rowEnd);
    }
}
