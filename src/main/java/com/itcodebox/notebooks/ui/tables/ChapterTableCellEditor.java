package com.itcodebox.notebooks.ui.tables;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.ChapterService;
import com.itcodebox.notebooks.service.impl.ChapterServiceImpl;
import com.itcodebox.notebooks.ui.notify.NotifyUtil;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.Objects;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class ChapterTableCellEditor extends DefaultCellEditor implements TableCellEditor {
    private Chapter chapter;
    private final JTextField textField;
    private final Project project;
    private final ChapterService chapterService = ChapterServiceImpl.getInstance();

    public ChapterTableCellEditor(Project project, JTextField textField) {
        super(textField);
        this.project = project;
        this.textField = textField;
        setClickCountToStart(200);
    }

    @Override
    public boolean stopCellEditing() {
        //如果存在同名chapter, 那么不可以停止编辑;
        String title = textField.getText();
        if (title.trim().isEmpty()) {
            NotifyUtil.showWarningNotification(
                    project,
                    "com.itcodebox.chapterTable.rename",
                    message("notify.renameFailed.title"),
                    "<html><body>" +
                            message("notify.renameFailed.message1.chapter") +
                            "<span style='color:red;font-weight: 700;'>" + title + " </span>" +
                            message("notify.renameFailed.message2.null") +
                            "</body></html>");
            textField.setText(chapter.getTitle());
        } else if (!title.equals(chapter.getTitle()) && (chapterService.findByTitle(title, chapter.getNotebookId()) != null)) {
            NotifyUtil.showWarningNotification(
                    project,
                    "com.itcodebox.chapterTable.rename",
                    message("notify.renameFailed.title"),
                    "<html><body>" +
                            message("notify.renameFailed.message1.chapter") +
                            "<span style='color:red;font-weight: 700;'>" + title + "</span>" +
                            message("notify.renameFailed.message2.exists") +
                            "<br />" +
                            message("notify.renameFailed.message3") +
                            "<span style='color:blue;font-weight: 700;'>" + chapter.getTitle() + "</span>" +
                            message("notify.renameFailed.message4") +
                            "<span style='color:red;font-weight: 700;'>" + title + "</span>" +
                            message("notify.renameFailed.message5") +
                            "</body></html>");

            //恢复之前的标题
            textField.setText(chapter.getTitle());
        }
        return super.stopCellEditing();
    }

    @Override
    public Object getCellEditorValue() {
        String text = textField.getText();
        if (Objects.equals(text, chapter.getTitle())) {
            return chapter;
        }
        chapter.setTitle(text);
        chapterService.update(chapter);
        ApplicationManager
                .getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onChapterTitleUpdated(project, chapter);
        return chapter;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        chapter = (Chapter) value;
        textField.setText(chapter.getTitle());
        textField.selectAll();
        return textField;
    }
}
