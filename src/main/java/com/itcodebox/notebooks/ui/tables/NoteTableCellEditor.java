package com.itcodebox.notebooks.ui.tables;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.NoteService;
import com.itcodebox.notebooks.service.impl.NoteServiceImpl;
import com.itcodebox.notebooks.ui.notify.NotifyUtil;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.Objects;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class NoteTableCellEditor extends DefaultCellEditor implements TableCellEditor {
    private Note note;
    private final JTextField textField;
    private final Project project;
    private final NoteService noteService =NoteServiceImpl.getInstance();

    public NoteTableCellEditor(Project project, JTextField textField) {
        super(textField);
        this.project = project;
        this.textField = textField;
        setClickCountToStart(200);
    }

    @Override
    public boolean stopCellEditing() {
        String title = textField.getText();
        if (title.trim().isEmpty()) {
            NotifyUtil.showWarningNotification(
                    project,
                    "com.itcodebox.noteTable.rename",
                    message("notify.renameFailed.title"),
                    "<html><body>" +
                            message("notify.renameFailed.message1.note") +
                            "<span style='color:red;font-weight: 700;'>" + title + " </span>" +
                            message("notify.renameFailed.message2.null") +
                            "</body></html>");
            textField.setText(note.getTitle());
            //如果修改了标题,并且新标题已经存在,那么提示重复
        } else if (!title.equals(note.getTitle()) && (noteService.findByTitle(title, note.getChapterId()) != null)) {
            NotifyUtil.showWarningNotification(
                    project,
                    "com.itcodebox.noteTable.rename",
                    message("notify.renameFailed.title"),
                    "<html><body>" +
                            message("notify.renameFailed.message1.note") +
                            "<span style='color:red;font-weight: 700;'>" + title + "</span>" +
                            message("notify.renameFailed.message2.exists") +
                            "<br />" +
                            message("notify.renameFailed.message3") +
                            "<span style='color:blue;font-weight: 700;'>" + note.getTitle() + "</span>" +
                            message("notify.renameFailed.message4") +
                            "<span style='color:red;font-weight: 700;'>" + title + "</span>" +
                            message("notify.renameFailed.message5") +
                            "</body></html>");

            //恢复之前的标题
            textField.setText(note.getTitle());
        }
        return super.stopCellEditing();
    }

    @Override
    public Object getCellEditorValue() {
        String text = textField.getText();
        if (Objects.equals(text, note.getTitle())) {
            return note;
        }
        note.setTitle(text);
        NoteService noteService = NoteServiceImpl.getInstance();
        noteService.update(note);
        ApplicationManager
                .getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onNoteTitleUpdated(project, note);

        return note;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        note = (Note) value;
        textField.setText(note.getTitle());
        textField.selectAll();
        return textField;
    }
}
