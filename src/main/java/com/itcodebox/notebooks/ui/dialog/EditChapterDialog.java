package com.itcodebox.notebooks.ui.dialog;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBLabel;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.entity.Notebook;
import com.itcodebox.notebooks.projectservice.NotebooksUIManager;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.ChapterService;
import com.itcodebox.notebooks.service.NoteService;
import com.itcodebox.notebooks.service.NotebookService;
import com.itcodebox.notebooks.service.impl.ChapterServiceImpl;
import com.itcodebox.notebooks.service.impl.NoteServiceImpl;
import com.itcodebox.notebooks.service.impl.NotebookServiceImpl;
import com.itcodebox.notebooks.utils.CustomUIUtil;
import com.itcodebox.notebooks.utils.StringUtil;
import icons.PluginIcons;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class EditChapterDialog extends DialogWrapper {
    private static final int DEFAULT_WIDTH = 380;
    private static final int DEFAULT_HEIGHT = 190;

    private final DefaultComboBoxModel<String>
            notebookTitleModel = new DefaultComboBoxModel<>();
    private final ComboBox<String> comboBoxNotebookTitle = new ComboBox<>(notebookTitleModel);
    protected DefaultComboBoxModel<String>
            chapterTitleModel = new DefaultComboBoxModel<>();
    protected ComboBox<String> comboBoxChapterTitle = new ComboBox<>(chapterTitleModel);

    private final NotebookService notebookService = NotebookServiceImpl.getInstance();
    protected ChapterService chapterService = ChapterServiceImpl.getInstance();
    protected NoteService noteService = NoteServiceImpl.getInstance();

    private final Project project;
    private final String notebookTitle;
    private final String chapterTitle;
    private final String noteTitle;
    private final NotebooksUIManager uiManger;


    public EditChapterDialog(Project project, String notebookTitle, String chapterTile, @Nullable String noteTitle) {
        super(true);
        this.project = project;
        uiManger = project.getService(NotebooksUIManager.class);
        this.notebookTitle = notebookTitle;
        this.chapterTitle = chapterTile;
        this.noteTitle = noteTitle;
        setTitle(message("editChapterDialog.title"));
        setOKButtonText(message("editChapterDialog.button.ok"));
        setOKButtonIcon(PluginIcons.Save);
        setCancelButtonText(message("button.cancel"));
        getRootPane().setMinimumSize(new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT));
        //getRootPane().setPreferredSize(new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT));
        init();
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{getOKAction(), getCancelAction()};
    }

    @Override
    protected void doOKAction() {
        String newNotebookTitle = (String) comboBoxNotebookTitle.getSelectedItem();
        String newChapterTitle = (String) comboBoxChapterTitle.getSelectedItem();
        boolean updatedTitle = !notebookTitle.equals(newNotebookTitle) || !chapterTitle.equals(newChapterTitle);

        if (updatedTitle) {

            Notebook notebook = notebookService.findByTitle(notebookTitle);
            Chapter chapter = chapterService.findByTitle(chapterTitle, notebook.getId());

            //笔记本标题和章节都修改了
            if (!notebookTitle.equals(newNotebookTitle) && !chapterTitle.equals(newChapterTitle)) {
                //如果只修改了笔记本的名字; 那么就是移动笔记本即可
                Notebook newNotebook = notebookService.findByTitle(newNotebookTitle);
                long time = System.currentTimeMillis();
                //如果新的笔记本不存在, 那么创建他并通知
                if (newNotebook == null) {
                    newNotebook = notebookService.insert(new Notebook(newNotebookTitle, time));
                    ApplicationManager.getApplication().getMessageBus()
                            .syncPublisher(RecordListener.TOPIC)
                            .onNotebookAdd(project, newNotebook, true, false);
                } else {
                    uiManger.getMainPanel().getNotebookTable().selectedRow(newNotebook);
                }
                chapter.setTitle(newChapterTitle);
                chapterService.update(chapter);
                ApplicationManager
                        .getApplication()
                        .getMessageBus()
                        .syncPublisher(RecordListener.TOPIC)
                        .onChapterTitleUpdated(project, chapter);
                moveToNotebook(newNotebook, chapter);
            } else if (notebookTitle.equals(newNotebookTitle)) {
                //如果只修改了章节名字
                chapter.setTitle(newChapterTitle);
                chapterService.update(chapter);
                ApplicationManager
                        .getApplication()
                        .getMessageBus()
                        .syncPublisher(RecordListener.TOPIC)
                        .onChapterTitleUpdated(project, chapter);
            } else {
                //如果只修改了笔记本的名字; 那么就是移动笔记本即可
                Notebook newNotebook = notebookService.findByTitle(newNotebookTitle);
                long time = System.currentTimeMillis();
                //如果新的笔记本不存在, 那么创建他并通知
                if (newNotebook == null) {
                    newNotebook = notebookService.insert(new Notebook(newNotebookTitle, time));
                    ApplicationManager.getApplication().getMessageBus()
                            .syncPublisher(RecordListener.TOPIC)
                            .onNotebookAdd(project, newNotebook, true, false);
                } else {
                    uiManger.getMainPanel().getNotebookTable().selectedRow(newNotebook);
                }
                moveToNotebook(newNotebook, chapter);
            }
        }
        super.doOKAction();
    }

    private void moveToNotebook(@NotNull Notebook targetNotebook, @NotNull Chapter sourceChapter) {
        //1. 查找该Chapter下的全部Note
        List<Note> noteList = noteService.findAllByChapterId(sourceChapter.getId());
        //2. 从数据库和视图中 删除该Chapter
        chapterService.delete(sourceChapter.getId());
        ApplicationManager
                .getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onChapterRemoved(project, sourceChapter);
        //3. 修改chapter的bookid
        sourceChapter.setNotebookId(targetNotebook.getId());
        //4. 插入更新后的chapter
        Chapter targetChapter = chapterService.insert(sourceChapter);
        ApplicationManager
                .getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onChapterAdd(project, targetChapter, true, false);
        //5. 更新Note的 从属关系
        for (Note note : noteList) {
            note.setNotebookId(targetNotebook.getId());
            note.setChapterId(targetChapter.getId());
        }
        Note[] noteAry = new Note[noteList.size()];
        //6. 向数据库插入新的notes
        noteService.insert(noteList.toArray(noteAry));
        ApplicationManager.getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onNoteAdd(project, noteList);
        if (noteTitle != null) {
            uiManger.getMainPanel().getNoteTable().selectedRowByTitle(noteTitle);
        }
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        comboBoxNotebookTitle.setEditable(true);
        notebookTitleModel.addAll(notebookService.getTitles());
        comboBoxChapterTitle.setEditable(true);
        comboBoxNotebookTitle.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedChapterTitle = (String) comboBoxChapterTitle.getSelectedItem();
                chapterTitleModel.removeAllElements();
                chapterTitleModel.addAll(chapterService.getTitles((String) comboBoxNotebookTitle.getSelectedItem()));
                chapterTitleModel.setSelectedItem(selectedChapterTitle);
            }
        });
        JPanel panel = new JPanel(new MigLayout(new LC().fill().gridGap("0!", "0!").insets("0")));
        panel.add(new JBLabel(message("addNoteDialog.label.notebookTitle"), PluginIcons.NotebookCell, JBLabel.LEFT));
        panel.add(comboBoxNotebookTitle, new CC().growX().push().wrap());
        panel.add(new JBLabel(message("addNoteDialog.label.chapterTitle"), PluginIcons.ChapterCell, JBLabel.LEFT));
        panel.add(comboBoxChapterTitle, new CC().growX().push().wrap());

        notebookTitleModel.setSelectedItem(notebookTitle);
        chapterTitleModel.setSelectedItem(chapterTitle);
        return panel;
    }

    //@Override
    //public @Nullable JComponent getPreferredFocusedComponent() {
    //    if (StringUtil.isEmptyOrNull(notebookTitle)) {
    //        return comboBoxNotebookTitle;
    //    } else {
    //        return comboBoxChapterTitle;
    //    }
    //}

    @Override
    protected @NotNull List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> infos = new ArrayList<>();
        String notebookTitleTemp = CustomUIUtil.getComboBoxText(comboBoxNotebookTitle);
        //1. notebookTitle为空警告
        if (StringUtil.isEmptyOrNull(notebookTitleTemp)) {
            infos.add(new ValidationInfo(message("addNoteDialog.validate.message"), comboBoxNotebookTitle));
        }
        String chapterTitleTemp = CustomUIUtil.getComboBoxText(comboBoxChapterTitle);
        //2. chapterTitle为空警告
        if (StringUtil.isEmptyOrNull(chapterTitleTemp)) {
            infos.add(new ValidationInfo(message("addNoteDialog.validate.message"), comboBoxChapterTitle));
        } else if (!StringUtil.isEmptyOrNull(notebookTitleTemp)) {
            Notebook notebookTemp = notebookService.findByTitle(notebookTitleTemp);
            //3. 冲突警告 如果修改了其中的一个标题才继续判断
            boolean updatedTitle = !notebookTitle.equals(notebookTitleTemp) || !chapterTitle.equals(chapterTitleTemp);
            if (updatedTitle && notebookTemp != null && chapterService.findByTitle(chapterTitleTemp, notebookTemp.getId()) != null) {
                infos.add(new ValidationInfo(message("addChapterDialog.validate.titleConflict"), comboBoxChapterTitle));
            }
        }
        return infos;
    }
}
