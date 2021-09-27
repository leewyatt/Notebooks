package com.itcodebox.notebooks.ui.tables;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.itcodebox.notebooks.entity.Notebook;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.NotebookService;
import com.itcodebox.notebooks.service.impl.NotebookServiceImpl;

/**
 * @author LeeWyatt
 */
public class NotebookTableDragMoveHandler extends AbstractTableMoveTransferHandler<Notebook> {

    public NotebookTableDragMoveHandler(Project project, NotebookTable table) {
        super(project, table, Notebook.class);
    }

    @Override
    protected void updateOrderInDataBase(Notebook[] ary) {
        NotebookService notebookService = NotebookServiceImpl.getInstance();
        notebookService.update(ary);
        //通知改变(其实UI上看不出来改变) 因为这里修改的就是showOrder
        ApplicationManager.getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onNotebookUpdated(project,ary);
    }


    @Override
    protected void updateOrderInTable(int rowFrom, int rowEnd, Object data) {
        ApplicationManager.getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onNotebookDragMove(project,(Notebook)data,rowFrom,rowEnd);
    }
}
