package com.itcodebox.notebooks.ui.dialog;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.entity.Notebook;
import com.itcodebox.notebooks.projectservice.ProjectStorage;
import com.itcodebox.notebooks.service.ChapterService;
import com.itcodebox.notebooks.service.NoteService;
import com.itcodebox.notebooks.service.NotebookService;
import com.itcodebox.notebooks.service.impl.ChapterServiceImpl;
import com.itcodebox.notebooks.service.impl.NoteServiceImpl;
import com.itcodebox.notebooks.service.impl.NotebookServiceImpl;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.regex.Pattern;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public  class BaseNoteDialog extends DialogWrapper {
    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 300;
    public Project project;
    protected final JBTextField fieldRange = new JBTextField();
    protected TextFieldWithBrowseButton fieldPath = new TextFieldWithBrowseButton();
    protected DefaultComboBoxModel<String>
            notebookTitleModel = new DefaultComboBoxModel<>();
    protected ComboBox<String> comboBoxNotebookTitle = new ComboBox<>(notebookTitleModel);
    protected DefaultComboBoxModel<String> chapterTitleModel = new DefaultComboBoxModel<>();
    protected ComboBox<String> comboBoxChapterTitle = new ComboBox<>(chapterTitleModel);
    protected DefaultComboBoxModel<String> noteTitleModel = new DefaultComboBoxModel<>();
    protected ComboBox<String> comboBoxNoteTitle = new ComboBox<>(noteTitleModel);
    protected NoteService noteService = NoteServiceImpl.getInstance();
    protected ChapterService chapterService = ChapterServiceImpl.getInstance();
    protected NotebookService notebookService = NotebookServiceImpl.getInstance();

    protected Note note;

    /**
     * 通过查询判断, 那个组件应该获得焦点, 可以为null
     */
    protected JComponent focusedComponent;
    protected final static String RANGE_REGEX = "\\s*\\d{1,9}\\s*:\\s*\\d{1,9}\\s*";
    protected  Pattern rangePattern = Pattern.compile(RANGE_REGEX);

    public BaseNoteDialog(Project project, @NotNull Note note) {
        super(project);
        this.project = project;
        this.note = note;
        initControls();
        fillData();

    }

    @Override
    protected void doHelpAction() {
        Messages.showInfoMessage(message("addNoteDialog.help.message"), message("addNoteDialog.help.title"));
    }

    private void initControls() {
        getRootPane().setMinimumSize(new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT));
        //getRootPane().setPreferredSize(new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT));
        comboBoxNotebookTitle.setEditable(true);
        comboBoxChapterTitle.setEditable(true);
        comboBoxNoteTitle.setEditable(true);
        comboBoxNotebookTitle.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }
            Notebook notebook = notebookService.findByTitle((String) comboBoxNotebookTitle.getSelectedItem());
            String chapterTitle = (String) comboBoxChapterTitle.getSelectedItem();
            chapterTitleModel.removeAllElements();
            if (notebook == null) {
                chapterTitleModel.setSelectedItem(chapterTitle);
                return;
            }
            chapterTitleModel.addAll(chapterService.getTitles(notebook.getTitle()));
            comboBoxChapterTitle.setSelectedItem(chapterTitle);
        });

        comboBoxChapterTitle.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }
            String noteTitle = (String) comboBoxNoteTitle.getSelectedItem();
            noteTitleModel.removeAllElements();
            Notebook notebook = notebookService.findByTitle((String) comboBoxNotebookTitle.getSelectedItem());
            if (notebook == null) {
                noteTitleModel.setSelectedItem(noteTitle);
                return;
            }
            Chapter chapter = chapterService.findByTitle((String) comboBoxChapterTitle.getSelectedItem(), notebook.getId());
            if (chapter == null) {
                noteTitleModel.setSelectedItem(noteTitle);
                return;
            }
            //List<String> noteTitleList = noteService.getTitles(notebook.getTitle(), chapter.getTitle());
            //for (String s : noteTitleList) {
            //    noteTitleModel.addElement(s);
            //}
            noteTitleModel.addAll(noteService.getTitles(notebook.getTitle(), chapter.getTitle()));

            noteTitleModel.setSelectedItem(noteTitle);
        });

        fieldPath.addBrowseFolderListener(
                message("addNoteDialog.fileChooser.title"),
                message("addNoteDialog.fileChooser.description"),
                project,
                new FileChooserDescriptor(true, true, true, true, true, false)
        );

        init();
    }

    public void fillData() {
        notebookTitleModel.addAll(notebookService.getTitles());
        //List<String> titles = notebookService.getTitles();
        //for (String title : titles) {
        //    notebookTitleModel.addElement(title);
        //}
        final ProjectStorage projectStorage =project.getService(ProjectStorage.class);
        //判断Notebook是否为null, 如果为null,那么首先获得焦点的组件应该是comboBoxNotebookTitle;
        Notebook notebook = null;
        if (projectStorage.selectedNotebookId >=0) {
            notebook = notebookService.findById(projectStorage.selectedNotebookId);
        }
        if (notebook == null) {
            focusedComponent = comboBoxNotebookTitle;
        }
        comboBoxNotebookTitle.setSelectedItem(notebook==null?"":notebook.getTitle());

        //判断chapter是否为null,以及comboBoxNoteTitle是否该取得焦点
        Chapter chapter = null;
        if (projectStorage.selectedChapterId>=0) {
            chapter=chapterService.findById(projectStorage.selectedChapterId);
        }
        if (notebook != null && chapter == null) {
            focusedComponent=comboBoxChapterTitle;
        }
        comboBoxChapterTitle.setSelectedItem(chapter == null ? "" : chapter.getTitle());

        //判断comboBoxNoteTitle是否该取得焦点
        if (notebook != null && chapter != null) {
            focusedComponent=comboBoxNoteTitle;
        }
        comboBoxNoteTitle.setSelectedItem("");
        fieldPath.setText(note.getSource() == null ? project.getBasePath() : note.getSource());
        //把光标位置移动到前面
        //textPaneContent.setCaretPosition(0);
        fieldRange.setText(note.getOffsetStart()+" : "+note.getOffsetEnd());
    }




    @Override
    protected @Nullable
    JComponent createCenterPanel() {
        //comboBoxNotebookTitle.setMinimumAndPreferredWidth(4096);
        //comboBoxChapterTitle.setMinimumAndPreferredWidth(4096);
        //comboBoxNoteTitle.setMinimumAndPreferredWidth(4096);
        JPanel p1 = new JPanel(new BorderLayout());
        p1.add(comboBoxNotebookTitle);      
        JPanel p2 = new JPanel(new BorderLayout());
        p2.add(comboBoxChapterTitle);
        JPanel p3 = new JPanel(new BorderLayout());
        p3.add(comboBoxNoteTitle);
        return FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel(message("addNoteDialog.label.notebookTitle"), PluginIcons.NotebookCell, JBLabel.LEFT), p1)
                .addLabeledComponent(new JBLabel(message("addNoteDialog.label.chapterTitle"), PluginIcons.ChapterCell, JBLabel.LEFT), p2)
                .addLabeledComponent(new JBLabel(message("addNoteDialog.label.noteTitle"), PluginIcons.NoteCell, JBLabel.LEFT), p3)
                .addLabeledComponent(new JBLabel(message("addNoteDialog.label.sourcePath"), PluginIcons.Link, JBLabel.LEFT), fieldPath)
                //.addComponentToRightColumn(new JBLabel("默认选择当前打开的文件作为参考路径"))
                .addLabeledComponent(new JBLabel(message("addNoteDialog.label.codeRange"),PluginIcons.CodeRange,JBLabel.LEFT), fieldRange)
                .addComponent(new JPanel())
                .getPanel();
    }

}
