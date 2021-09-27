package com.itcodebox.notebooks.ui.dialog;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.*;
import com.intellij.ui.components.panels.HorizontalBox;
import com.intellij.util.ui.JBEmptyBorder;
import com.itcodebox.notebooks.constant.PluginColors;
import com.itcodebox.notebooks.entity.SearchMode;
import com.itcodebox.notebooks.entity.SearchRecord;
import com.itcodebox.notebooks.projectservice.NotebooksUIManager;
import com.itcodebox.notebooks.service.impl.SearchRecordServiceImpl;
import com.itcodebox.notebooks.ui.component.CodeEditorUtil;
import com.itcodebox.notebooks.ui.tables.ChapterTable;
import com.itcodebox.notebooks.ui.tables.NoteTable;
import com.itcodebox.notebooks.ui.tables.NotebookTable;
import com.itcodebox.notebooks.utils.StringUtil;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class SearchDialog extends DialogWrapper {
    private static final int DEFAULT_WIDTH = 600;
    private static final int DEFAULT_HEIGHT = 630;

    private final SearchTextField searchField = new SearchTextField();
    private final Editor fieldContent;
    private final JBTextArea fieldDesc = new JBTextArea();
    private final SearchRecordCellRender cellRender = new SearchRecordCellRender();

    private final ComboBox<String> comboBox = new ComboBox<String>(
            new String[]{message("searchDialog.comboBox.all"), message("searchDialog.comboBox.note"), message("searchDialog.comboBox.chapter"), message("searchDialog.comboBox.notebook")}
    );
    private final DefaultListModel<SearchRecord> listModel = new DefaultListModel<>();
    private final JBList<SearchRecord> recordListView = new JBList<>(listModel);
    private final JBCheckBox checkBoxContent = new JBCheckBox(message("searchDialog.checkBox.content"), true);
    private final JBCheckBox checkBoxDescription = new JBCheckBox(message("searchDialog.checkBox.description"), true);
    private final Project project;
    private final JBLabel resultLabel = new JBLabel();

    public SearchDialog(Project project) {
        super(true);
        this.project = project;
        fieldContent = CodeEditorUtil.createCodeEditor(project);
        Disposer.register(project, () -> EditorFactory.getInstance().releaseEditor(fieldContent));
        getRootPane().setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        //getRootPane().setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setTitle(message("searchDialog.title"));
        setOKButtonText(message("searchDialog.button.ok"));
        init();
    }

    @Override
    protected void doOKAction() {
        SearchRecord selectedValue = recordListView.getSelectedValue();
        if (selectedValue == null) {
            super.doOKAction();
            return;
        }

        NotebooksUIManager uiManager = project.getService(NotebooksUIManager.class);
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Notebook");
        if (toolWindow == null) {
            super.doOKAction();
            return;
        }else {
            if (!toolWindow.isVisible()) {
                toolWindow.show(null);
            }
        }
        NotebookTable notebookTable = uiManager.getMainPanel().getNotebookTable();
        if (selectedValue.getNotebookId() != null) {
            notebookTable.selectedRowById(selectedValue.getNotebookId());
        } else {
            notebookTable.clearSelection();
        }
        ChapterTable chapterTable = uiManager.getMainPanel().getChapterTable();
        if (selectedValue.getChapterId() != null) {
            chapterTable.selectedRowById(selectedValue.getChapterId());
        } else {
            chapterTable.clearSelection();
        }
        NoteTable noteTable = uiManager.getMainPanel().getNoteTable();
        if (selectedValue.getNoteId() != null) {
            noteTable.selectedRowById(selectedValue.getNoteId());
        } else {
            noteTable.clearSelection();
        }
        super.doOKAction();
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return searchField;
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{getOKAction(), getCancelAction()};
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JComponent topPanel = buildCenterTopPanel();
        JPanel lvPane = new JPanel(new BorderLayout());
        JBLabel listTitleLabel = new JBLabel(message("searchDialog.label.searchResult"), AllIcons.General.Locate, JBLabel.LEFT);
        HorizontalBox hBox = new HorizontalBox();
        hBox.add(listTitleLabel);
        hBox.add(resultLabel);
        hBox.setBorder(new JBEmptyBorder(2, 0, 3, 0));
        lvPane.add(hBox, BorderLayout.NORTH);
        recordListView.setCellRenderer(cellRender);
        recordListView.addListSelectionListener(e -> {
            SearchRecord selectedSearchRecord = recordListView.getSelectedValue();
            if (selectedSearchRecord == null) {
                fieldDesc.setText("");
                CodeEditorUtil.setCode(project, fieldContent, "");
                return;
            }
            fieldDesc.setText(selectedSearchRecord.getDescription() == null ? "" : selectedSearchRecord.getDescription());
            CodeEditorUtil.setCode(project, fieldContent, selectedSearchRecord.getContent() == null ? "" : selectedSearchRecord.getContent());
            CodeEditorUtil.setEditorHighlighter(fieldContent, selectedSearchRecord.getType() == null ? "" : selectedSearchRecord.getType());
        });
        lvPane.add(new JBScrollPane(recordListView));
        JPanel row1 = new JPanel(new BorderLayout());
        row1.add(topPanel,BorderLayout.NORTH);
        row1.add(lvPane);
        JBTabbedPane row2 = new JBTabbedPane();
        row2.addTab(message("detailPanel.label.content"), PluginIcons.Description, fieldContent.getComponent());
        row2.addTab(message("detailPanel.label.description"), PluginIcons.Code, new JBScrollPane(fieldDesc));

        JBSplitter p = new JBSplitter(true, 0.6F);
        p.setFirstComponent(row1);
        p.setSecondComponent(row2);

        return p;
    }

    @NotNull
    private JComponent buildCenterTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchField);

        searchField.getTextEditor().addActionListener(e -> doSearch());
        //先默认选择Note吧
        comboBox.setSelectedIndex(1);
        comboBox.addItemListener(e -> {
            if (ItemEvent.SELECTED == e.getStateChange()) {
                int selectedIndex = comboBox.getSelectedIndex();
                checkBoxContent.setEnabled(selectedIndex == 0 || selectedIndex == 1);
                checkBoxDescription.setEnabled(selectedIndex == 0 || selectedIndex == 1);
                doSearch();
            }
        });
        checkBoxContent.addItemListener(e -> doSearch());
        checkBoxDescription.addItemListener(e -> doSearch());
        JPanel westPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        westPanel.add(comboBox);
        westPanel.add(checkBoxContent);
        westPanel.add(checkBoxDescription);
        topPanel.add(westPanel, BorderLayout.WEST);

        return topPanel;
    }

    private void doSearch() {
        searchField.addCurrentTextToHistory();
        String text = searchField.getText();
        listModel.clear();
        //添加数据
        List<SearchRecord> records = SearchRecordServiceImpl.getInstance().searchKeywords(text, getSearchMode());
        if (records != null) {
            if (records.size() == 0) {
                resultLabel.setForeground(PluginColors.WARN_COLOR);
            }else {
                resultLabel.setForeground(PluginColors.NOTE_GROUP_SELECTED);
            }
            resultLabel.setText(" "+records.size()+ message("searchDialog.label.searchResultText"));
            listModel.addAll(records);
            if (records.size() != 0) {
                recordListView.requestFocus();
                recordListView.setSelectedIndex(0);
            }
            cellRender.setKeywords(StringUtil.splitKeywords(text));
        }
    }

    @NotNull
    private SearchMode getSearchMode() {
        SearchMode searchMode = SearchMode.All;
        int selectedIndex = comboBox.getSelectedIndex();
        if (selectedIndex == 0) {
            if (checkBoxContent.isSelected() && checkBoxDescription.isSelected()) {
                searchMode = SearchMode.AllAndContentAndDescription;
            } else if (!checkBoxContent.isSelected() && !checkBoxDescription.isSelected()) {
                searchMode = SearchMode.All;
            } else if (checkBoxContent.isSelected()) {
                searchMode = SearchMode.AllAndContent;
            } else {
                searchMode = SearchMode.AllAndDescription;
            }
        }
        if (selectedIndex == 1) {
            if (checkBoxContent.isSelected() && checkBoxDescription.isSelected()) {
                searchMode = SearchMode.NoteAndContentAndDescription;
            } else if (!checkBoxContent.isSelected() && !checkBoxDescription.isSelected()) {
                searchMode = SearchMode.Note;
            } else if (checkBoxContent.isSelected()) {
                searchMode = SearchMode.NoteAndContent;
            } else {
                searchMode = SearchMode.NoteAndDescription;
            }
        }
        if (selectedIndex == 2) {
            searchMode = SearchMode.Chapter;
        }
        if (selectedIndex == 3) {
            searchMode = SearchMode.Notebook;
        }
        return searchMode;
    }

}
