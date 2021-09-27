package com.itcodebox.notebooks.ui.panes;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.entity.Notebook;
import com.itcodebox.notebooks.projectservice.ProjectStorage;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.ChapterService;
import com.itcodebox.notebooks.service.NotebookService;
import com.itcodebox.notebooks.service.impl.ChapterServiceImpl;
import com.itcodebox.notebooks.service.impl.NotebookServiceImpl;
import com.itcodebox.notebooks.ui.tables.AbstractRecordTable;
import com.itcodebox.notebooks.ui.tables.ChapterTable;
import com.itcodebox.notebooks.ui.tables.NotebookTable;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class NotebookPanel extends JPanel implements FocusListener{
    private final ChapterTable chapterTable;
    private final NotebookTable notebookTable;
    private final ProjectStorage projectStorage;
    private final JButton btnAddNotebook;

    private boolean isCascadeOperation;

    public void setCascadeOperation(boolean cascadeOperation) {
        isCascadeOperation = cascadeOperation;
    }

    public NotebookPanel(Project project, NotebookTable notebookTable, ChapterTable chapterTable, DetailPanel detailPanel) {
        projectStorage =project.getService( ProjectStorage.class);
        this.notebookTable = notebookTable;
        this.chapterTable = chapterTable;
        btnAddNotebook = new JButton(message("button.addNotebook"), AllIcons.General.Add);
        setReadOnly(AppSettingsState.getInstance().readOnlyMode);
        btnAddNotebook.addActionListener(event -> {
            long millis = System.currentTimeMillis();
            //一. 数据库里添加
            NotebookService notebookService =NotebookServiceImpl.getInstance();
            Notebook notebook = notebookService.insert(new Notebook(message("simple.text.notebook") + millis, millis));
            //二. 通知界面去修改
            ApplicationManager
                    .getApplication()
                    .getMessageBus()
                    .syncPublisher(RecordListener.TOPIC)
                    .onNotebookAdd(project, notebook, true, true);
        });
        reloadData();
        setLayout(new BorderLayout());
        add(btnAddNotebook, BorderLayout.SOUTH);
        add(new JBScrollPane(notebookTable, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        //恢复成上下移动的模式模式
        notebookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                notebookTable.setDropMode(DropMode.INSERT_ROWS);
                notebookTable.setRecordDragMode(AbstractRecordTable.RecordDragMode.MOVE_UP_OR_DOWN);
            }
        });
        notebookTable.getSelectionModel().addListSelectionListener(e -> {
            Notebook notebook = notebookTable.getSelectedObject();
            projectStorage.selectedNotebookId = (notebook == null ? -1 : notebook.getId());
            refreshChapterTable(notebook);
            detailPanel.refreshComboBoxModel(RefreshType.Notebook);
        });

        setShowFocusBorder(AppSettingsState.getInstance().showFocusBorder);
    }

    public void setShowFocusBorder(boolean showFocusBorder) {
        if (showFocusBorder) {
            setBorder(PluginConstant.FOCUS_LOST_BORDER);
            notebookTable.addFocusListener(this);
        }else{
            notebookTable.removeFocusListener(this);
            setBorder(PluginConstant.FOCUS_LOST_BORDER);
        }
    }

    public void reloadData() {
        NotebookService service = NotebookServiceImpl.getInstance();
        notebookTable.clearRows();
        List<Notebook> items = service.findAll();
        if (items != null) {
            notebookTable.getRecordModel().setItems(items);
        }
    }

    public void setReadOnly(boolean isReadOnly) {
        btnAddNotebook.setEnabled(!isReadOnly);
    }

    private void refreshChapterTable(Notebook notebook) {
        if (notebook != null) {
            ChapterService service = ChapterServiceImpl.getInstance();
            List<Chapter> list = service.findAllByNotebookId(notebook.getId());
            if (list != null) {
                chapterTable.getRecordModel().setItems(list);
                if (isCascadeOperation) {
                    chapterTable.selectedFirst();
                }
            } else {
                chapterTable.clearRows();
            }
        } else {
            chapterTable.clearRows();
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        setBorder(PluginConstant.FOCUS_GAINED_BORDER);
    }

    @Override
    public void focusLost(FocusEvent e) {
        setBorder(PluginConstant.FOCUS_LOST_BORDER);
    }
}
