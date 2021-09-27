package com.itcodebox.notebooks.action;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.BalloonImpl;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import com.itcodebox.notebooks.constant.PluginColors;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.entity.Notebook;
import com.itcodebox.notebooks.service.ChapterService;
import com.itcodebox.notebooks.service.NoteService;
import com.itcodebox.notebooks.service.NotebookService;
import com.itcodebox.notebooks.service.impl.ChapterServiceImpl;
import com.itcodebox.notebooks.service.impl.NoteServiceImpl;
import com.itcodebox.notebooks.service.impl.NotebookServiceImpl;
import icons.PluginIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class InsertPanelFactory {
    private final DefaultComboBoxModel<String>
            notebookTitleModel = new DefaultComboBoxModel<>();
    private final ComboBox<String> comboBoxNotebookTitle = new ComboBox<>(notebookTitleModel);
    private final DefaultComboBoxModel<String> chapterTitleModel = new DefaultComboBoxModel<>();
    private final ComboBox<String> comboBoxChapterTitle = new ComboBox<>(chapterTitleModel);
    private final DefaultComboBoxModel<String> noteTitleModel = new DefaultComboBoxModel<>();
    private final ComboBox<String> comboBoxNoteTitle = new ComboBox<>(noteTitleModel);
    private final NoteService noteService = NoteServiceImpl.getInstance();
    private final ChapterService chapterService = ChapterServiceImpl.getInstance();
    private final NotebookService notebookService = NotebookServiceImpl.getInstance();
    private final JBLabel tipLabel = new JBLabel(" ");
    private final JButton btnInsert = new JButton(message("insertBalloon.insertButton.text"));
    private  Note note;
    private BalloonImpl balloon;

    public void setBalloonImpl(BalloonImpl balloon){
        this.balloon = balloon;
    }
    public  JPanel createInsertPanel(Project project, Editor editor) {
        tipLabel.setForeground(PluginColors.WARN_COLOR);
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flowPanel.add(btnInsert);
        btnInsert.setEnabled(false);
        btnInsert.addActionListener(e->{
            // 注意判断笔记是否为空, 当前选择的Editor是否可以写
            if (note == null || editor == null || !editor.getDocument().isWritable()) {
                return;
            }
            Document document = editor.getDocument();
            int offset = editor.getCaretModel().getPrimaryCaret().getOffset();
            ApplicationManager.getApplication().runWriteAction(() -> WriteCommandAction.runWriteCommandAction(project,
                    () -> document.insertString(offset, note.getContent())));
            if (balloon != null) {
                balloon.hide();
            }
        });
        comboBoxNotebookTitle.setMinimumAndPreferredWidth(190);
        JPanel topPane = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel(message("addNoteDialog.label.notebookTitle"), PluginIcons.NotebookCell, JBLabel.LEFT), getComboBoxPanel(comboBoxNotebookTitle))
                .addLabeledComponent(new JBLabel(message("addNoteDialog.label.chapterTitle"), PluginIcons.ChapterCell, JBLabel.LEFT), getComboBoxPanel(comboBoxChapterTitle))
                .addLabeledComponent(new JBLabel(message("addNoteDialog.label.noteTitle"), PluginIcons.NoteCell, JBLabel.LEFT), getComboBoxPanel(comboBoxNoteTitle))
                .addComponent(tipLabel)
                .addComponent(flowPanel)
                .getPanel();
        comboBoxAddItemListener();
        comboBoxNotebookFillData();
        return topPane;
    }

    private JPanel getComboBoxPanel(JComponent component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(component);
        return panel;
    }

    private void comboBoxAddItemListener() {
        comboBoxNotebookTitle.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }
            Notebook notebook = notebookService.findByTitle((String) comboBoxNotebookTitle.getSelectedItem());
            chapterTitleModel.removeAllElements();
            noteTitleModel.removeAllElements();
            if (notebook == null) {
                return;
            }
            List<String> chapterTitleList = chapterService.getTitles(notebook.getTitle());
            for (String s : chapterTitleList) {
                chapterTitleModel.addElement(s);
            }
            validateNote();
        });

        comboBoxChapterTitle.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }
            noteTitleModel.removeAllElements();
            Notebook notebook = notebookService.findByTitle((String) comboBoxNotebookTitle.getSelectedItem());
            if (notebook == null) {
                return;
            }
            Chapter chapter = chapterService.findByTitle((String) comboBoxChapterTitle.getSelectedItem(), notebook.getId());
            if (chapter == null) {
                return;
            }
            List<String> noteTitleList = noteService.getTitles(notebook.getTitle(), chapter.getTitle());
            for (String s : noteTitleList) {
                noteTitleModel.addElement(s);
            }
            validateNote();
        });

        comboBoxNoteTitle.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }
            validateNote();
        });
    }

    private void comboBoxNotebookFillData() {
        List<String> titles = notebookService.getTitles();
        for (String title : titles) {
            notebookTitleModel.addElement(title);
        }
    }

    private void validateNote() {
        this.note = noteService.findByTitles(
                (String) comboBoxNoteTitle.getSelectedItem(),
                (String) comboBoxChapterTitle.getSelectedItem(),
                (String) comboBoxNotebookTitle.getSelectedItem()
             );
        if (note == null) {
            tipLabel.setText(" ");
            btnInsert.setEnabled(false);
        }else{
            btnInsert.setEnabled(!note.getContent().isEmpty());
            if (note.getContent().isEmpty()) {
                tipLabel.setText(message("insertBalloon.tipLabel.contentEmpty"));
            }else{
                tipLabel.setText(" ");
            }
        }
    }
}
