package com.itcodebox.notebooks.ui.tables;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.itcodebox.notebooks.entity.Notebook;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.NotebookService;
import com.itcodebox.notebooks.service.impl.NotebookServiceImpl;
import com.itcodebox.notebooks.ui.notify.NotifyUtil;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.Objects;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;
/**
 * @author LeeWyatt
 */
public class NotebookTableCellEditor extends DefaultCellEditor implements TableCellEditor {
    private Notebook notebook;
    private final JTextField textField;
    private final Project project;
    private final NotebookService notebookService = NotebookServiceImpl.getInstance();

    public NotebookTableCellEditor(Project project, JTextField textField) {
        super(textField);
        this.project = project;
        this.textField = textField;
        setClickCountToStart(200);
    }

    @Override
    public boolean stopCellEditing() {
        //如果存在同名notebook, 那么不可以停止编辑
        String title = textField.getText();
        if (title.trim().isEmpty()) {
            NotifyUtil.showWarningNotification(
                    project,
                    "com.itcodebox.notebookTable.rename",
                    message("notify.renameFailed.title"),
                    "<html><body>" +
                            message("notify.renameFailed.message1.notebook") +
                            "<span style='color:red;font-weight: 700;'>" + title + " </span>" +
                            message("notify.renameFailed.message2.null") +
                            "</body></html>");
            textField.setText(notebook.getTitle());
        }else if ((notebookService.findByTitle(title) != null) && !notebook.getTitle().equals(title)) {
            NotifyUtil.showWarningNotification(
                    project,
                    "com.itcodebox.notebookTable.rename",
                    message("notify.renameFailed.title"),
                    "<html><body>" +
                            message("notify.renameFailed.message1.notebook") +
                            "<span style='color:red;font-weight: 700;'>" + title + " </span>" +
                            message("notify.renameFailed.message2.exists") +
                            "<br />" +
                            message("notify.renameFailed.message3") +
                            "<span style='color:blue;font-weight: 700;'>" + notebook.getTitle() + " </span>" +
                            message("notify.renameFailed.message4") +
                            "<span style='color:red;font-weight: 700;'>" + title + "</span>" +
                            message("notify.renameFailed.message5") +
                            "</body></html>");
            textField.setText(notebook.getTitle());
        }
        return super.stopCellEditing();
    }

    @Override
    public Object getCellEditorValue() {
        String text = textField.getText();
        if (Objects.equals(text, notebook.getTitle())) {
            return notebook;
        }
        notebook.setTitle(text);
        //一. 数据库里修改
        notebookService.update(notebook);
        //二. 通知UI修改
        ApplicationManager
                .getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onNotebookTitleUpdated(project, notebook);
        return notebook;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        notebook = (Notebook) value;
        textField.setText(notebook.getTitle());
        textField.selectAll();
        return textField;
    }
}
