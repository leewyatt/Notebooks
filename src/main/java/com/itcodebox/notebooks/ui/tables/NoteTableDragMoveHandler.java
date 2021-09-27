package com.itcodebox.notebooks.ui.tables;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.NoteService;
import com.itcodebox.notebooks.service.impl.NoteServiceImpl;

/**
 * @author LeeWyatt
 */
public class NoteTableDragMoveHandler extends AbstractTableMoveTransferHandler<Note> {

    public NoteTableDragMoveHandler(Project project, NoteTable table) {
        super(project, table, Note.class);
    }

    @Override
    protected void updateOrderInDataBase(Note[] ary) {
        NoteService notebookService = NoteServiceImpl.getInstance();
        notebookService.update(ary);
        //通知改变(其实UI上看不出来改变) 因为这里修改的就是showOrder
        ApplicationManager.getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onNoteUpdated(project,ary);
    }


    @Override
    protected void updateOrderInTable(int rowFrom, int rowEnd, Object data) {
        ApplicationManager.getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onNoteDragMove(project,(Note)data,rowFrom,rowEnd);
    }
}
