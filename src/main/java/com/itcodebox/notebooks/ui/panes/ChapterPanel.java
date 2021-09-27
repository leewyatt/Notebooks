package com.itcodebox.notebooks.ui.panes;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.entity.Notebook;
import com.itcodebox.notebooks.projectservice.ProjectStorage;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.ChapterService;
import com.itcodebox.notebooks.service.NoteService;
import com.itcodebox.notebooks.service.NotebookService;
import com.itcodebox.notebooks.service.impl.ChapterServiceImpl;
import com.itcodebox.notebooks.service.impl.NoteServiceImpl;
import com.itcodebox.notebooks.service.impl.NotebookServiceImpl;
import com.itcodebox.notebooks.ui.tables.AbstractRecordTable;
import com.itcodebox.notebooks.ui.tables.ChapterTable;
import com.itcodebox.notebooks.ui.tables.NoteTable;
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
public class ChapterPanel extends JPanel implements FocusListener{
    private final NoteTable noteTable;
    private final ProjectStorage projectStorage ;
    private final JButton btnAddChapter;
    private final ChapterTable chapterTable;
    private boolean isCascadeOperation;


    public void setCascadeOperation(boolean cascadeOperation) {
        isCascadeOperation = cascadeOperation;
    }
    public ChapterPanel(Project project, NotebookTable notebookTable, ChapterTable chapterTable, NoteTable noteTable, DetailPanel detailPanel) {
        projectStorage =project.getService( ProjectStorage.class);
        this.noteTable = noteTable;
        this.chapterTable=chapterTable;
        btnAddChapter = new JButton(message("button.addChapter"), AllIcons.General.Add);
        setReadOnly(AppSettingsState.getInstance().readOnlyMode);
        btnAddChapter.addActionListener(event -> {
            long time = System.currentTimeMillis();
            Notebook notebook = notebookTable.getSelectedObject();
            // 如果没有选择任何的Notebook 那么就创建一个默认的
            if (notebook == null) {
                NotebookService notebookService =NotebookServiceImpl.getInstance();
                notebook = notebookService.insert(new Notebook(message("simple.text.notebook")+ time, time));
                ApplicationManager
                        .getApplication()
                        .getMessageBus()
                        .syncPublisher(RecordListener.TOPIC)
                        .onNotebookAdd(project,notebook, true, false);
            }
            Integer bookId = notebook.getId();
            ChapterService chapterService = ChapterServiceImpl.getInstance();
            Chapter chapter = chapterService.insert(new Chapter(bookId, message("simple.text.chapter")+ time, time));
            ApplicationManager
                    .getApplication()
                    .getMessageBus()
                    .syncPublisher(RecordListener.TOPIC)
                    .onChapterAdd(project,chapter, true, true);
        });

        setLayout(new BorderLayout());
        add(btnAddChapter, BorderLayout.SOUTH);
        add(new JBScrollPane(chapterTable, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        //恢复成上下移动的模式模式
        chapterTable.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                chapterTable.setDropMode(DropMode.INSERT_ROWS);
                chapterTable.setRecordDragMode(AbstractRecordTable.RecordDragMode.MOVE_UP_OR_DOWN);
            }
        });

        chapterTable.getSelectionModel().addListSelectionListener(e -> {
            Chapter chapter = chapterTable.getSelectedObject();
            projectStorage.selectedChapterId=(chapter==null?-1:chapter.getId());
            refreshNoteTable(chapter);
            detailPanel.refreshComboBoxModel(RefreshType.Notebook);
        });

        setShowFocusBorder(AppSettingsState.getInstance().showFocusBorder);
    }

    public void setShowFocusBorder(boolean showFocusBorder) {
        if (showFocusBorder) {
            setBorder(PluginConstant.FOCUS_LOST_BORDER);
            chapterTable.addFocusListener(this);
        }else{
            chapterTable.removeFocusListener(this);
            setBorder(PluginConstant.FOCUS_LOST_BORDER);
        }
    }



    public void setReadOnly(boolean isReadOnly) {
        btnAddChapter.setEnabled(!isReadOnly);
    }

    private void refreshNoteTable(Chapter chapter) {
        if (chapter != null) {
            NoteService service = NoteServiceImpl.getInstance();
            List<Note> list = service.findAllByChapterId(chapter.getId());
            if (list != null) {
                noteTable.getRecordModel().setItems(list);
                if (isCascadeOperation) {
                    noteTable.selectedFirst();
                }
            } else {
                noteTable.clearRows();
            }
        } else {
            noteTable.clearRows();
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
