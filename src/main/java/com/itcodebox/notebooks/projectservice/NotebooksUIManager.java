package com.itcodebox.notebooks.projectservice;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.util.ui.ListTableModel;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.entity.Notebook;
import com.itcodebox.notebooks.ui.panes.DetailPanel;
import com.itcodebox.notebooks.ui.panes.MainPanel;
import com.itcodebox.notebooks.ui.panes.RefreshType;
import com.itcodebox.notebooks.ui.tables.ChapterTable;
import com.itcodebox.notebooks.ui.tables.NoteTable;
import com.itcodebox.notebooks.ui.tables.NotebookTable;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsChangedListener;
import com.itcodebox.notebooks.utils.ImportUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Objects;

/**
 * @author LeeWyatt
 */
@SuppressWarnings("unchecked")
public class NotebooksUIManager {
    private MainPanel mainPanel;
    private final Project project;
    private final ProjectStorage projectStorage;
    private boolean isReadOnlyMode = false;


    /**
     * 项目级别的Service 支持注入Project 作为参数; 其余参数不支持
     *
     * @param project 当前工程
     */
    public NotebooksUIManager(Project project) {
        this.project = project;
        projectStorage = project.getService(ProjectStorage.class);
        subscribeRecordTopic();
        subscribeSettingsTopic();

        ProjectManager.getInstance().addProjectManagerListener(project, new ProjectManagerListener() {

            @Override
            public void projectClosing(@NotNull Project project) {
                //if (mainPanel != null) {
                //    mainPanel.getDetailPanel().dispose();
                //}
                if (isReadOnlyMode) {
                    ImportUtil.publishReadOnlyMode(project, false);
                }

                //if (mainPanel != null) {
                //    ToolWindow toolWindow = mainPanel.getToolWindow();
                //    if (toolWindow != null) {
                //        ContentManager contentManager = toolWindow.getContentManager();
                //        contentManager.dispose();
                //    }
                //}
            }
        });

    }

    public MainPanel getMainPanel() {
        return mainPanel;
    }

    public void setMainPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    private void subscribeSettingsTopic() {
        //Application接受的话,销毁麻烦
        project.getMessageBus()
                .connect()
                .subscribe(AppSettingsChangedListener.TOPIC, new AppSettingsChangedListener() {
                    @Override
                    public void onSetItemExpandable(boolean expandable) {
                        if (mainPanel != null) {
                            mainPanel.getNotebookTable().setExpandableItemsEnabled(expandable);
                            mainPanel.getChapterTable().setExpandableItemsEnabled(expandable);
                            mainPanel.getNoteTable().setExpandableItemsEnabled(expandable);
                        }
                    }

                    @Override
                    public void onSetCustomFont(Font font) {
                        if (mainPanel != null) {
                            //mainPanel.getDetailPanel().getFieldContent().setFont(font);
                            mainPanel.getDetailPanel().getFieldDesc().setFont(font);
                        }
                    }

                    @Override
                    public void onSetReadOnlyMode(Project eventProject, boolean isReadOnly) {
                        if (eventProject == project) {
                            isReadOnlyMode = isReadOnly;
                        }
                        if (mainPanel != null) {
                            mainPanel.getNotebookPanel().setReadOnly(isReadOnly);
                            mainPanel.getChapterPanel().setReadOnly(isReadOnly);
                            mainPanel.getNotePanel().setReadOnly(isReadOnly);
                            //mainPanel.getDetailPanel().setReadOnly(isReadOnly);
                        }
                    }

                    @Override
                    public void onSetShowFocusBorder(boolean show) {
                        if (mainPanel != null) {
                            mainPanel.getNotebookPanel().setShowFocusBorder(show);
                            mainPanel.getChapterPanel().setShowFocusBorder(show);
                            mainPanel.getNotePanel().setShowFocusBorder(show);
                        }
                    }

                });
    }

    private void subscribeRecordTopic() {
        project.getMessageBus()
                .connect()
                .subscribe(RecordListener.TOPIC, new RecordListener() {

                    @Override
                    public void onRefresh() {
                        onRefreshHandler();
                    }

                    /**
                     * 添加Notebook Chapter Note需要判断线程是不是调度线程,
                     * 因为有可能是用后台线程从JSon里Import进来的
                     * 现在已经不需要判断了是否是调度线程插入的数据了
                     * 因为一个一个的数据插入会非常慢, 改成了使用全部条件完毕后 ,重新刷新读取数据
                     * onRefresh()方法来解决
                     * Application application = ApplicationManager.getApplication();
                     *                         if (application.isDispatchThread()) {
                     *                             //改变UI界面;
                     *                         } else {
                     *                             application.invokeLater(() -> 改变UI界面);
                     *                         }
                     */
                    @Override
                    public void onNotebookAdd(Project eventProject, Notebook notebook, boolean isSelected, boolean editing) {
                        onNotebookAddHandler(eventProject, notebook, isSelected, editing);
                    }

                    @Override
                    public void onNotebookUpdated(Project eventProject, Notebook... notebooks) {
                        onNotebookUpdatedHandler(eventProject, notebooks);
                    }

                    @Override
                    public void onNotebookTitleUpdated(Project eventProject, Notebook notebook) {
                        onNotebookTitleUpdatedHandler(eventProject, notebook);
                    }

                    @Override
                    public void onNotebookRemoved(Project eventProject, Notebook notebook) {
                        onNotebookRemovedHandler(eventProject, notebook);
                    }

                    @Override
                    public void onNotebookDragMove(Project eventProject, Notebook notebook, int rowFromIndex, int rowEndIndex) {
                        onNotebookDragMoveHandler(eventProject, notebook, rowFromIndex, rowEndIndex);
                    }

                    @Override
                    public void onChapterAdd(Project eventProject, Chapter chapter, boolean isSelected, boolean editing) {
                        onChapterAddHandler(eventProject, chapter, isSelected, editing);
                    }

                    @Override
                    public void onChapterUpdated(Project eventProject, Chapter... chapters) {
                        onChapterUpdatedHandler(eventProject, chapters);
                    }

                    @Override
                    public void onChapterTitleUpdated(Project eventProject, Chapter chapter) {
                        onChapterTitleUpdatedHandler(eventProject, chapter);
                    }

                    @Override
                    public void onChapterRemoved(Project eventProject, Chapter chapter) {
                        onChapterRemovedHandler(eventProject, chapter);
                    }

                    @Override
                    public void onChapterDragMove(Project eventProject, Chapter chapter, int rowFromIndex, int rowEndIndex) {
                        onChapterDragMoveHandler(eventProject, chapter, rowFromIndex, rowEndIndex);
                    }

                    @Override
                    public void onNoteAdd(Project eventProject, Note note) {
                        onNoteAddHandler(eventProject, note);
                    }

                    @Override
                    public void onNoteAdd(Project eventProject, List<Note> list) {
                        onNoteBatchAddHandler(eventProject, list);
                    }

                    @Override
                    public void onNoteUpdated(Project eventProject, Note... notes) {
                        onNoteUpdatedHandler(eventProject, notes);
                    }

                    @Override
                    public void onNoteTitleUpdated(Project eventProject, Note note) {
                        onNoteTitleUpdatedHandler(eventProject, note);
                    }

                    @Override
                    public void onNoteRemoved(Project eventProject, Note note) {
                        onNoteRemovedHandler(eventProject, note);
                    }

                    @Override
                    public void onNoteDragMove(Project eventProject, Note note, int rowFromIndex, int rowEndIndex) {
                        onNoteDragMoveHandler(eventProject, note, rowFromIndex, rowEndIndex);
                    }
                });
    }

    private void onRefreshHandler() {
        if (mainPanel == null) {
            return;
        }
        NotebookTable notebookTable = mainPanel.getNotebookTable();
        int notebookId = notebookTable.getSelectedRecordId();
        ChapterTable chapterTable = mainPanel.getChapterTable();
        int chapterId = chapterTable.getSelectedRecordId();
        NoteTable noteTable = mainPanel.getNoteTable();
        int noteId = noteTable.getSelectedRecordId();
        notebookTable.clearRows();
        mainPanel.getNotebookPanel().reloadData();
        DetailPanel detailPanel = mainPanel.getDetailPanel();
        mainPanel.getChapterPanel().setCascadeOperation(false);
        mainPanel.getNotebookPanel().setCascadeOperation(false);
        if (notebookId != -1) {
            notebookTable.selectedRowById(notebookId);
        }
        if (chapterId != -1) {
            chapterTable.selectedRowById(chapterId);
        }
        if (noteId != -1) {
            noteTable.selectedRowById(noteId);
        }
        detailPanel.refreshComboBoxModel(RefreshType.Notebook);
        mainPanel.getChapterPanel().setCascadeOperation(true);
        mainPanel.getNotebookPanel().setCascadeOperation(true);

    }

    private void onNotebookAddHandler(Project eventProject, Notebook notebook, boolean isSelected, boolean editing) {
        //二. 更新界面
        //1. 如果这个事件源,不是当前客户端, 那么只添加一行即可
        if (eventProject != project) {
            if (mainPanel != null) {
                mainPanel.getNotebookTable().addRow(notebook);
                mainPanel.getDetailPanel().refreshComboBoxModel(RefreshType.Notebook);
            }
            return;
        }
        //2. 如果这个事件源, 是当前客户端. 但是当前客户端并未打开ToolWindow
        if (mainPanel != null) {
            if (isSelected) {
                // 如果这个事件源, 是当前客户端,并且已经打开了ToolWindow 那么需要选择它
                mainPanel.getNotebookTable().addAndSelected(notebook, editing);
            } else {
                mainPanel.getNotebookTable().addRow(notebook);
            }
            mainPanel.getDetailPanel().refreshComboBoxModel(RefreshType.Notebook);
        }
        if (isSelected) {
            projectStorage.selectedNotebookId = notebook.getId();
        }
    }

    public void onNotebookUpdatedHandler(Project eventProject, Notebook... notebooks) {
        //更新UI
        if (mainPanel == null || eventProject == project) {
            return;
        }
        NotebookTable notebookTable = mainPanel.getNotebookTable();
        Notebook selectedNotebook = notebookTable.getSelectedObject();
        NoteTable noteTable = mainPanel.getNoteTable();
        Note note = noteTable.getSelectedObject();
        notebookTable.stopEditing();
        for (Notebook notebook : notebooks) {
            Notebook item = notebookTable.findById(notebook.getId());
            if (item != null) {
                //如果修改了标题, 并且是NoteTable所选择的Note对应的Notebook,那么刷新detailPane的显示
                String oldTile = item.getTitle();
                String newTitle = notebook.getTitle();
                item.setTitle(newTitle);
                item.setCreateTime(notebook.getCreateTime());
                item.setUpdateTime(notebook.getUpdateTime());
                item.setShowOrder(notebook.getShowOrder());
            }
        }
    }

    private void onNotebookTitleUpdatedHandler(Project eventProject, Notebook notebook) {
        if (mainPanel == null || notebook == null) {
            return;
        }
        NotebookTable notebookTable = mainPanel.getNotebookTable();
        Notebook item = notebookTable.findById(notebook.getId());
        // 刷新NotebookTable的显示
        if (item != null) {
            item.setTitle(notebook.getTitle());
        }
    }

    private void onChapterTitleUpdatedHandler(Project eventProject, Chapter chapter) {
        if (mainPanel == null || chapter == null) {
            return;
        }
        Notebook selectedNotebook = mainPanel.getNotebookTable().getSelectedObject();
        if (selectedNotebook == null || !Objects.equals(selectedNotebook.getId(), chapter.getNotebookId())) {
            return;
        }
        //刷新ChapterTable显示
        ChapterTable chapterTable = mainPanel.getChapterTable();
        Chapter item = chapterTable.findById(chapter.getId());
        if (item != null) {
            item.setTitle(chapter.getTitle());
        }
    }

    private void onNoteTitleUpdatedHandler(Project eventProject, Note note) {
        if (mainPanel == null || note == null) {
            return;
        }
        Chapter selectedChapter = mainPanel.getChapterTable().getSelectedObject();

        if (selectedChapter == null || !Objects.equals(selectedChapter.getId(), note.getChapterId())) {
            return;
        }
        NoteTable noteTable = mainPanel.getNoteTable();
        Note item = noteTable.findById(note.getId());
        Note selectedNote = noteTable.getSelectedObject();
        //刷新NoteTable里的标题显示
        if (item != null) {
            item.setTitle(note.getTitle());
        }

    }

    private void onNotebookRemovedHandler(Project eventProject, Notebook notebook) {
        if (mainPanel == null) {
            return;
        }
        //获取删除前的选择状态
        NotebookTable notebookTable = mainPanel.getNotebookTable();
        Notebook selectedNotebook = notebookTable.getSelectedObject();
        ChapterTable chapterTable = mainPanel.getChapterTable();
        Chapter selectedChapter = chapterTable.getSelectedObject();
        NoteTable noteTable = mainPanel.getNoteTable();
        Note selectedNote = noteTable.getSelectedObject();
        // 移除条目
        notebookTable.removeRow(notebook);

        //恢复选择
        if (notebook != selectedNotebook && selectedNotebook != null) {
            notebookTable.selectedRow(selectedNotebook);
            if (selectedChapter != null) {
                chapterTable.selectedRow(selectedChapter);
                if (selectedNote != null) {
                    noteTable.selectedRow(selectedNote);
                }
            }
        }

    }

    private void onNotebookDragMoveHandler(Project eventProject, Notebook notebook, int rowFromIndex, int rowEndIndex) {
        // eventProject 的事件处理 和数据库处理已经在TableDragMoveHandler里进行了处理
        if (mainPanel == null) {
            return;
        }
        if (eventProject == project) {
            mainPanel.getDetailPanel().refreshComboBoxModel(RefreshType.Notebook);
            return;
        }
        onDragMove(mainPanel.getNotebookTable().getRecordModel(), true, notebook, rowFromIndex, rowEndIndex);
    }

    private void onChapterAddHandler(Project eventProject, Chapter chapter, boolean isSelected, boolean editing) {
        if (mainPanel == null) {
            projectStorage.selectedChapterId = chapter.getId();
            return;
        }
        //二. 更新界面
        Notebook notebook = mainPanel.getNotebookTable().getSelectedObject();
        if (notebook != null && Objects.equals(chapter.getNotebookId(), notebook.getId())) {
            if (eventProject == project && isSelected) {
                mainPanel.getChapterTable().addAndSelected(chapter, editing);
            } else {
                // 不是当前工程 ,所以只添加, 而不去选择和编辑
                mainPanel.getChapterTable().addRow(chapter);
            }
            mainPanel.getDetailPanel().refreshComboBoxModel(RefreshType.Notebook);
        }

    }

    private void onChapterRemovedHandler(Project project, Chapter chapter) {
        if (mainPanel == null) {
            return;
        }
        //获取删除前的选择状态
        NotebookTable notebookTable = mainPanel.getNotebookTable();
        Notebook selectedNotebook = notebookTable.getSelectedObject();
        ChapterTable chapterTable = mainPanel.getChapterTable();
        Chapter selectedChapter = chapterTable.getSelectedObject();
        NoteTable noteTable = mainPanel.getNoteTable();
        Note selectedNote = noteTable.getSelectedObject();
        //删除
        chapterTable.removeRow(chapter);

        //恢复选择
        if (selectedNotebook != null) {
            notebookTable.selectedRow(selectedNotebook);
            if (chapter != selectedChapter && selectedChapter != null) {
                chapterTable.selectedRow(selectedChapter);
                if (selectedNote != null) {
                    noteTable.selectedRow(selectedNote);
                }
            }
        }
    }

    private void onChapterUpdatedHandler(Project eventProject, Chapter... chapters) {
        //更新UI
        if (mainPanel == null || eventProject == project || chapters.length == 0) {
            return;
        }
        ChapterTable chapterTable = mainPanel.getChapterTable();
        chapterTable.stopEditing();
        Notebook notebook = mainPanel.getNotebookTable().getSelectedObject();
        if (notebook == null || !Objects.equals(chapters[0].getNotebookId(), notebook.getId())) {
            return;
        }
        // chapters[0] 这需要确保处理的chapters 全部都是一个NotebookId下.
        for (Chapter chapter : chapters) {
            Chapter temp = chapterTable.findById(chapter.getId());
            if (temp != null) {
                temp.setNotebookId(chapter.getNotebookId());
                temp.setTitle(chapter.getTitle());
                temp.setCreateTime(chapter.getCreateTime());
                temp.setUpdateTime(chapter.getUpdateTime());
                temp.setShowOrder(chapter.getShowOrder());
            }
        }
    }

    private void onChapterDragMoveHandler(Project eventProject, Chapter chapter, int rowFromIndex, int rowEndIndex) {
        if (mainPanel == null) {
            return;
        }
        if (eventProject == project) {
            mainPanel.getDetailPanel().refreshComboBoxModel(RefreshType.Notebook);
            return;
        }
        int selectedNotebookId = mainPanel.getNotebookTable().getSelectedRecordId();
        // 如果显示的是同一个Notebook的章节, 那么才会影响UI
        boolean flag = selectedNotebookId != -1 && chapter.getNotebookId() == selectedNotebookId;
        onDragMove(mainPanel.getChapterTable().getRecordModel(), flag, chapter, rowFromIndex, rowEndIndex);
    }

    private void onDragMove(@NotNull ListTableModel model, boolean updateModel, Object t, int rowFromIndex, int rowEndIndex) {
        //1. 获取组件
        NotebookTable notebookTable = mainPanel.getNotebookTable();
        ChapterTable chapterTable = mainPanel.getChapterTable();
        NoteTable noteTable = mainPanel.getNoteTable();
        //2. 获取选择的item
        Notebook selectedNotebook = notebookTable.getSelectedObject();
        Chapter selectedChapter = chapterTable.getSelectedObject();
        Note selectedNote = noteTable.getSelectedObject();
        //挪动位置一定要判断是不是受影响的条目被选择了
        if (updateModel) {
            //3. 挪动位置(就是先删除,然后在指定的位置插入)
            model.removeRow(rowFromIndex);
            // 注意插入的是被拖动的行,而不是当前被动接受的工程里选择的行
            model.insertRow(rowEndIndex, t);
        }
        //4. 恢复选择 如果当前工程选择的Notebook, 和被动接受工程里选择都是同一个Notebook; 但是实际上model的变化,也会影响选择,导致子级发生改变
        notebookTable.selectedRow(selectedNotebook);
        chapterTable.selectedRow(selectedChapter);
        noteTable.selectedRow(selectedNote);
        if (updateModel) {
            mainPanel.getDetailPanel().refreshComboBoxModel(RefreshType.Notebook);
        }
    }

    private void onNoteAddHandler(Project eventProject, Note note) {
        if (eventProject == project) {
            projectStorage.selectedNoteId = note.getId();
        }

        if (mainPanel == null) {
            return;
        }
        Notebook notebook = mainPanel.getNotebookTable().getSelectedObject();
        Chapter chapter = mainPanel.getChapterTable().getSelectedObject();
        if (notebook != null
                && chapter != null
                && Objects.equals(note.getNotebookId(), notebook.getId())
                && Objects.equals(note.getChapterId(), chapter.getId())) {
            if (eventProject == project) {
                mainPanel.getNoteTable().addAndSelected(note);
            } else {
                mainPanel.getNoteTable().addRow(note);
            }
            mainPanel.getDetailPanel().refreshComboBoxModel(RefreshType.Notebook);
        }
    }

    private void onNoteBatchAddHandler(Project eventProject, List<Note> noteList) {
        if (mainPanel == null || noteList == null || noteList.isEmpty()) {
            return;
        }
        Notebook notebook = mainPanel.getNotebookTable().getSelectedObject();
        Chapter chapter = mainPanel.getChapterTable().getSelectedObject();

        if (notebook != null
                && chapter != null
                && Objects.equals(noteList.get(0).getNotebookId(), notebook.getId())
                && Objects.equals(noteList.get(0).getChapterId(), chapter.getId())) {
            mainPanel.getNoteTable().addRow(noteList);
        }
    }

    private void onNoteRemovedHandler(Project eventProject, Note note) {
        if (mainPanel == null) {
            return;
        }
        //获取删除前的选择状态
        NotebookTable notebookTable = mainPanel.getNotebookTable();
        Notebook selectedNotebook = notebookTable.getSelectedObject();
        ChapterTable chapterTable = mainPanel.getChapterTable();
        Chapter selectedChapter = chapterTable.getSelectedObject();
        NoteTable noteTable = mainPanel.getNoteTable();
        Note selectedNote = noteTable.getSelectedObject();
        //删除
        noteTable.removeRow(note);

        //恢复选择
        if (selectedNotebook != null) {
            notebookTable.selectedRow(selectedNotebook);
            if (selectedChapter != null) {
                chapterTable.selectedRow(selectedChapter);
                if (note != selectedNote && selectedNote != null) {
                    noteTable.selectedRow(selectedNote);
                }
            }
        }
    }

    private void onNoteUpdatedHandler(Project eventProject, Note... notes) {
        //更新UI
        if (mainPanel == null || notes.length == 0) {
            return;
        }

        //1. UI界面上表的显示
        NoteTable noteTable = mainPanel.getNoteTable();
        noteTable.stopEditing();
        Notebook notebook = mainPanel.getNotebookTable().getSelectedObject();
        Chapter chapter = mainPanel.getChapterTable().getSelectedObject();
        if (notebook == null || chapter == null
                || !Objects.equals(notes[0].getNotebookId(), notebook.getId())
                || !Objects.equals(notes[0].getChapterId(), chapter.getId())) {
            return;
        }

        for (Note note : notes) {
            Note temp = noteTable.findById(note.getId());
            //如果当前Note 修改了 ,那么通知DetailPane进行修改
            if (temp != null) {
                temp.setNotebookId(note.getNotebookId());
                temp.setChapterId(note.getChapterId());
                temp.setTitle(note.getTitle());
                temp.setCreateTime(note.getCreateTime());
                temp.setUpdateTime(note.getUpdateTime());
                temp.setShowOrder(note.getShowOrder());
                temp.setContent(note.getContent());
                temp.setDescription(note.getDescription());
                temp.setSource(note.getSource());
                temp.setType(note.getType());
                temp.setImageRecords(note.getImageRecords());
                if (Objects.equals(temp.getId(), noteTable.getSelectedRecordId())) {
                    mainPanel.getDetailPanel().refreshDetail();
                }
            }

        }
    }

    private void onNoteDragMoveHandler(Project eventProject, Note note, int rowFromIndex, int rowEndIndex) {
        if (mainPanel == null) {
            return;
        }
        if (eventProject == project) {
            mainPanel.getDetailPanel().refreshComboBoxModel(RefreshType.Notebook);
            return;
        }
        int selectedChapterId = mainPanel.getChapterTable().getSelectedRecordId();
        boolean flag = selectedChapterId != -1 && selectedChapterId == note.getChapterId();
        onDragMove(mainPanel.getNoteTable().getRecordModel(), flag, note, rowFromIndex, rowEndIndex);
    }

}
