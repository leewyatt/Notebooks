package com.itcodebox.notebooks.ui.panes;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.entity.Notebook;
import com.itcodebox.notebooks.entity.Record;
import com.itcodebox.notebooks.projectservice.ProjectStorage;
import com.itcodebox.notebooks.ui.tables.*;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class MainPanel extends JPanel {

    private final AppSettingsState appSettingsState = AppSettingsState.getInstance();
    private Project project;
    private final ToolWindow toolWindow;
    private final NoteTable noteTable;
    private final ChapterTable chapterTable;
    private final NotebookTable notebookTable;
    private final NotePanel notePanel;
    private final ChapterPanel chapterPanel;
    private final NotebookPanel notebookPanel;
    private final DetailPanel detailPanel;
    private final ProjectStorage projectStorage;
    private final JBSplitter leftPane = new JBSplitter(false, 0.5f);
    private final JBSplitter rightPane = new JBSplitter(false, 0.5F);
    private final JBSplitter contentPane = new JBSplitter(false, 0.5f);



    public NotePanel getNotePanel() {
        return notePanel;
    }

    public ChapterPanel getChapterPanel() {
        return chapterPanel;
    }

    public NotebookPanel getNotebookPanel() {
        return notebookPanel;
    }

    public DetailPanel getDetailPanel() {
        return detailPanel;
    }

    public NoteTable getNoteTable() {
        return noteTable;
    }

    public ChapterTable getChapterTable() {
        return chapterTable;
    }

    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    public NotebookTable getNotebookTable() {
        return notebookTable;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }


    public JBSplitter getLeftPane() {
        return leftPane;
    }

    public JBSplitter getRightPane() {
        return rightPane;
    }

    public JBSplitter getContentPane() {
        return contentPane;
    }

    public MainPanel(Project project, @NotNull ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        projectStorage = project.getService(ProjectStorage.class);

        noteTable = new NoteTable(project);
        settingNoteTable();
        chapterTable = new ChapterTable(project);
        settingChapterTable();
        notebookTable = new NotebookTable(project);
        settingNotebookTable();

        detailPanel = new DetailPanel(project, notebookTable, chapterTable, noteTable);
        notePanel = new NotePanel(project, noteTable, detailPanel);
        chapterPanel = new ChapterPanel(project, notebookTable, chapterTable, noteTable, detailPanel);
        notebookPanel = new NotebookPanel(project, notebookTable, chapterTable, detailPanel);

        setLayout(new BorderLayout());
        //1. 创建东侧的工具栏(添加工具按钮)
        //initToolBar();

        //2. 创建左侧的布局
        leftPane.setFirstComponent(notebookPanel);
        leftPane.setSecondComponent(chapterPanel);

        //3. 创建右侧的布局
        rightPane.setFirstComponent(notePanel);
        rightPane.setSecondComponent(detailPanel);

        //4. 创建内容组件,并且设置
        contentPane.setFirstComponent(leftPane);
        contentPane.setSecondComponent(rightPane);
        add(contentPane);

        //1. 恢复组件的可见状态
        resetPanesVisible();
        //2. 恢复表格的选择状态(当用户设置了可以恢复选择时)
        if (appSettingsState.restoreSelected) {
            resetTableSelected();
        }
        //重构下Model ,否则当上次退出时没有选择会出现空下拉框的问题
        detailPanel.refreshComboBoxModel(RefreshType.Notebook);
        chapterPanel.setCascadeOperation(true);
        notebookPanel.setCascadeOperation(true);
        //3. 恢复表格的单元格是否可以扩展
        resetExpandableItems();
        //使用鼠标控制Table的选择
        keyControllerSelection();

    }

    private void keyControllerSelection() {
        noteTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (chapterPanel.isVisible() && e.getKeyCode() == KeyEvent.VK_LEFT) {
                    chapterTable.requestFocus();
                }
            }
        });

        chapterTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (notebookPanel.isVisible() && e.getKeyCode() == KeyEvent.VK_LEFT) {
                    notebookTable.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT && noteTable.getRowCount() > 0) {
                    noteTable.requestFocus();
                    if (noteTable.getSelectedObject() == null) {
                        noteTable.selectedFirst();
                    }
                }
            }
        });

        notebookTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT && chapterTable.getRowCount() > 0) {
                    chapterTable.requestFocus();
                    if (chapterTable.getSelectedObject() == null) {
                        chapterTable.selectedFirst();
                    }
                }
            }
        });
    }

    /**
     * 设置笔记本表格的模型: 表头,值,渲染器,编辑器...
     */
    private void settingNotebookTable() {
        notebookTable.setRecordModel(new ListTableModel<>(
                new ColumnInfo<Notebook, Notebook>(message("simple.text.notebook")) {

                    private final NotebookTableCellEditor notebookTableCellEditor = new NotebookTableCellEditor(project, new JBTextField());
                    private final NotebookTableCellRenderer notebookTableCellRenderer = new NotebookTableCellRenderer();

                    @Override
                    public @Nullable
                    Notebook valueOf(Notebook notebook) {
                        return notebook;
                    }

                    @Override
                    public boolean isCellEditable(Notebook notebook) {
                        return true;
                    }

                    @Override
                    public @Nullable
                    TableCellRenderer getRenderer(Notebook notebook) {
                        return notebook == null ? null : notebookTableCellRenderer;

                    }

                    @Override
                    public @Nullable
                    TableCellEditor getEditor(Notebook notebook) {
                        return notebookTableCellEditor;
                    }
                }
        ));

    }

    /**
     * 设置章节表格的模型: 表头,值,渲染器,编辑器...
     */
    private void settingChapterTable() {
        chapterTable.setRecordModel(new ListTableModel<>(
                new ColumnInfo<Chapter, Chapter>(message("simple.text.chapter")) {
                    private final ChapterTableCellRenderer chapterTableCellRenderer = new ChapterTableCellRenderer();
                    private final ChapterTableCellEditor chapterTableCellEditor = new ChapterTableCellEditor(project, new JBTextField());

                    @Override
                    public @Nullable
                    Chapter valueOf(Chapter chapter) {
                        return chapter;
                    }

                    @Override
                    public boolean isCellEditable(Chapter chapter) {
                        return true;
                    }

                    @Override
                    public @Nullable
                    TableCellRenderer getRenderer(Chapter chapter) {
                        return chapter == null ? null : chapterTableCellRenderer;
                    }

                    @Override
                    public @Nullable
                    TableCellEditor getEditor(Chapter chapter) {
                        return chapterTableCellEditor;
                    }

                }));

    }

    /**
     * 设置笔记表格的模型: 表头,值,渲染器,编辑器...
     */
    private void settingNoteTable() {
        noteTable.setRecordModel(new ListTableModel<>(
                new ColumnInfo<Note, Note>(message("simple.text.note")) {

                    private final NoteTableCellRenderer noteTableCellRenderer = new NoteTableCellRenderer();
                    private final NoteTableCellEditor noteTableCellEditor = new NoteTableCellEditor(project, new JBTextField());

                    @Nullable
                    @Override
                    public Note valueOf(Note note) {
                        return note;
                    }

                    @Override
                    public @Nullable
                    TableCellRenderer getRenderer(Note note) {
                        return note == null ? null : noteTableCellRenderer;
                    }

                    @Override
                    public @Nullable
                    TableCellEditor getEditor(Note note) {
                        return noteTableCellEditor;
                    }

                    @Override
                    public boolean isCellEditable(Note note) {
                        return true;
                    }
                }));
    }







    public void resetExpandableItems() {
        boolean expandable = appSettingsState.itemExpandable;
        notebookTable.setExpandableItemsEnabled(expandable);
        chapterTable.setExpandableItemsEnabled(expandable);
        noteTable.setExpandableItemsEnabled(expandable);
    }

    public void resetPanesVisible() {
        notebookPanel.setVisible(projectStorage.notebookPaneVisible);
        chapterPanel.setVisible(projectStorage.chapterPaneVisible);
        notePanel.setVisible(projectStorage.notePaneVisible);
    }

    /**
     * 恢复选择的状态
     */
    public void resetTableSelected() {
        int notebookId = projectStorage.selectedNotebookId;
        if (notebookId == -1 || !findRecordInTable(notebookTable, notebookId)) {
            return;
        }
        int chapterId = projectStorage.selectedChapterId;
        if (chapterId == -1 || !findRecordInTable(chapterTable, chapterId)) {
            return;
        }
        int noteId = projectStorage.selectedNoteId;
        if (noteId == -1) {
            return;
        }
        findRecordInTable(noteTable, noteId);
    }

    /**
     * 查找记录, 如果有就选中
     */
    private boolean findRecordInTable(@NotNull TableView<? extends Record> tableView, int id) {
        boolean findIt = false;
        List<? extends Record> items = tableView.getItems();
        for (int i = 0; i < items.size(); i++) {
            Record item = items.get(i);
            if (item.getId() == id || item.getId().equals(id)) {
                findIt = true;
                tableView.getSelectionModel().setSelectionInterval(i, i);
                break;
            }
        }
        return findIt;
    }

}
