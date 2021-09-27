package com.itcodebox.notebooks.ui.panes;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.projectservice.ProjectStorage;
import com.itcodebox.notebooks.ui.dialog.AddNoteDialog;
import com.itcodebox.notebooks.ui.tables.NoteTable;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class NotePanel extends JPanel implements FocusListener{
    private final Project project;
    private final JButton btnAddNote;
    private final NoteTable noteTable;
    public NotePanel(Project project, NoteTable noteTable, DetailPanel detailPanel) {
        this.project = project;
        this.noteTable = noteTable;
        ProjectStorage projectStorage =project.getService( ProjectStorage.class);
        setLayout(new BorderLayout());
        btnAddNote = new JButton(message("button.addNote"), AllIcons.General.Add);
        setReadOnly(AppSettingsState.getInstance().readOnlyMode);
        btnAddNote.addActionListener(e -> doAddNote());
        add(btnAddNote, BorderLayout.SOUTH);

        JBSplitter paneCenter = new JBSplitter(true, 0.7F);
        JBScrollPane scrollPane = new JBScrollPane(noteTable, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        paneCenter.setFirstComponent(scrollPane);
        add(paneCenter);
        // 当NoteTable选择改变时,存储选择位置 和 触发保存..
        noteTable.getSelectionModel().addListSelectionListener(e -> {
            Note note = noteTable.getSelectedObject();
            projectStorage.selectedNoteId = (note == null ? -1 : note.getId());
            detailPanel.refreshComboBoxModel(RefreshType.Notebook);
        });


        setShowFocusBorder(AppSettingsState.getInstance().showFocusBorder);
    }

    public void setShowFocusBorder(boolean showFocusBorder) {
        if (showFocusBorder) {
            setBorder(PluginConstant.FOCUS_LOST_BORDER);
            noteTable.addFocusListener(this);
        }else{
            noteTable.removeFocusListener(this);
            setBorder(PluginConstant.FOCUS_LOST_BORDER);
        }
    }

    public void setReadOnly(boolean isReadOnly) {
        btnAddNote.setEnabled(!isReadOnly);
    }
    public void doAddNote() {
        Note note = new Note();
        VirtualFile[] selectedFiles = FileEditorManager.getInstance(project).getSelectedFiles();
        if (selectedFiles.length>0) {
           note.setSource(selectedFiles[0].getPath());
        }
        new AddNoteDialog(project, note).show();
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
