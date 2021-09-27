package com.itcodebox.notebooks.ui.dialog;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.entity.Notebook;
import com.itcodebox.notebooks.projectservice.NotebooksUIManager;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.ui.panes.MainPanel;
import com.itcodebox.notebooks.utils.CustomUIUtil;
import com.itcodebox.notebooks.utils.StringUtil;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;
/**
 * @author LeeWyatt
 */
public class EditNoteDialog extends BaseNoteDialog {
    private final MainPanel mainPanel;

    public EditNoteDialog(Project project, Note note) {
        super(project, note);
        comboBoxNoteTitle.setSelectedItem(note.getTitle());
        NotebooksUIManager uiManger = project.getService(NotebooksUIManager.class);
        mainPanel = uiManger.getMainPanel();
        setTitle(message("updateNoteDialog.title"));
        setOKButtonText(message("updateNoteDialog.save"));
        setOKButtonIcon(PluginIcons.Save);
        setCancelButtonText(message("updateNoteDialog.cancel"));
    }

    @Override
    protected void doOKAction() {
        // 第一步 重新设置note的属性,并且判断是否有更改父目录
        int modifyType = setNoteProperty();
        //如果只是修改笔记的内容, 那么就是update即可
        if (modifyType == CHANGE_NOTE) {
            // 第二步 数据库更新
            noteService.update(note);
            //通知图形界面
            ApplicationManager
                    .getApplication()
                    .getMessageBus()
                    .syncPublisher(RecordListener.TOPIC)
                    .onNoteUpdated(project, new Note[]{note});
        } else if (modifyType == CHANGE_ALL || modifyType == CHANGE_PARENT) {
            noteService.delete(note.getId());
            ApplicationManager
                    .getApplication()
                    .getMessageBus()
                    .syncPublisher(RecordListener.TOPIC)
                    .onNoteRemoved(project, note);
            note = noteService.insert(note);
            ApplicationManager
                    .getApplication()
                    .getMessageBus()
                    .syncPublisher(RecordListener.TOPIC)
                    .onNoteAdd(project, note);

        }
        super.doOKAction();
    }

    private static final int NO_CHANGE = 0;
    private static final int CHANGE_PARENT = 1;
    private static final int CHANGE_NOTE = 2;
    private static final int CHANGE_ALL = 3;

    private int setNoteProperty() {
        int modifyType = -1;
        // 修改了从属,也修改了笔记的属性
        if (isModifyParent() && isModifyParent()) {
            modifyType = CHANGE_ALL;
        }
        //只修改了笔记的属性
         if (isModifyNote() && !isModifyParent()) {
            modifyType = CHANGE_NOTE;
        }
        //只修改了从属关系
         if (!isModifyNote() && isModifyParent()) {
            modifyType = CHANGE_PARENT;
        }
         if (!isModifyNote() && !isModifyParent()) {
            return NO_CHANGE;
        }

        String notebookName = (String) comboBoxNotebookTitle.getSelectedItem();
        Notebook notebook = notebookService.findByTitle(notebookName);
        if (notebook == null) {
            notebook = notebookService.insert(new Notebook(notebookName, System.currentTimeMillis()));
            ApplicationManager
                    .getApplication()
                    .getMessageBus()
                    .syncPublisher(RecordListener.TOPIC)
                    .onNotebookAdd(project, notebook, true, false);
        } else {
            // 如果不相等, 说明还是更改了notebook的;
            if (!notebook.getId().equals(note.getNotebookId())) {
                if (mainPanel != null) {
                    mainPanel.getNotebookTable().selectedRow(notebook);
                }
            }
        }

        String chapterTitle = (String) comboBoxChapterTitle.getSelectedItem();
        Chapter chapter = chapterService.findByTitle(chapterTitle, notebook.getId());
        if (chapter == null) {
            chapter = chapterService.insert(new Chapter(notebook.getId(), chapterTitle, System.currentTimeMillis()));
            ApplicationManager
                    .getApplication()
                    .getMessageBus()
                    .syncPublisher(RecordListener.TOPIC)
                    .onChapterAdd(project, chapter, true, false);
        } else {
            if (!chapter.getId().equals(note.getChapterId())) {
                if (mainPanel != null) {
                    mainPanel.getChapterTable().selectedRow(chapter);
                }
            }
        }
        note.setNotebookId(notebook.getId());
        note.setChapterId(chapter.getId());
        if (modifyType==CHANGE_NOTE||modifyType==CHANGE_ALL) {
            note.setUpdateTime(System.currentTimeMillis());
        }
        note.setTitle((String) comboBoxNoteTitle.getSelectedItem());
        note.setSource(fieldPath.getText());

        String str = fieldRange.getText();
        Matcher matcher = rangePattern.matcher(str);
        if (matcher.matches()) {
            String[] ss = str.split(":");
            note.setOffsetStart(Integer.parseInt(ss[0].trim()));
            note.setOffsetEnd(Integer.parseInt(ss[1].trim()));
        }else{
            note.setOffsetStart(0);
            note.setOffsetEnd(0);
        }
        return modifyType;
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{getHelpAction(),getOKAction(), getCancelAction()};
    }

    @Override
    protected @NotNull
    List<ValidationInfo> doValidateAll() {
        ArrayList<ValidationInfo> infos = new ArrayList<>();
        String notebookTitle = CustomUIUtil.getComboBoxText(comboBoxNotebookTitle);
        if (StringUtil.isEmptyOrNull(notebookTitle)) {
            infos.add(new ValidationInfo(message("updateNoteDialog.validate.message"), comboBoxNotebookTitle));
        }
        String chapterTitle = CustomUIUtil.getComboBoxText(comboBoxChapterTitle);
        if (StringUtil.isEmptyOrNull(chapterTitle)) {
            infos.add(new ValidationInfo(message("updateNoteDialog.validate.message"), comboBoxChapterTitle));
        }
        String noteTitle = CustomUIUtil.getComboBoxText(comboBoxNoteTitle);
        if (StringUtil.isEmptyOrNull(noteTitle)) {
            infos.add(new ValidationInfo(message("updateNoteDialog.validate.message"), comboBoxNoteTitle));
        } else if ( !StringUtil.isEmptyOrNull(notebookTitle) && !StringUtil.isEmptyOrNull(chapterTitle)) {
            Note noteResult = noteService.findByTitles(noteTitle, chapterTitle, notebookTitle);
            if (noteResult != null&& !noteResult.getId().equals(note.getId())) {
                infos.add(new ValidationInfo(message("updateNoteDialog.validate.noteTitleConflict"), comboBoxNoteTitle));
            }
        }
        Matcher matcher = rangePattern.matcher(fieldRange.getText());
        if (!matcher.matches()) {
            infos.add(new ValidationInfo(message("addNoteDialog.validate.rangeError")+RANGE_REGEX, fieldRange));
        }
        return infos;
    }

    /**
     * 判断笔记的从属关系 是否被修改
     *
     * @return 从属关系是否被修改
     */
    private boolean isModifyParent() {
        Notebook notebook = mainPanel.getNotebookTable().getSelectedObject();
        String notebookTitle = notebook == null ? "" : notebook.getTitle();
        Chapter chapter = mainPanel.getChapterTable().getSelectedObject();
        String chapterTitle = chapter == null ? "" : chapter.getTitle();

        String newNotebookTitle = (String) comboBoxNotebookTitle.getSelectedItem();
        String newChapterTitle = (String) comboBoxChapterTitle.getSelectedItem();

        return !Objects.equals(notebookTitle, newNotebookTitle)
                || !Objects.equals(chapterTitle, newChapterTitle);
    }

    /**
     * 笔记调整所属的章节或者笔记本,都不算修改
     *
     * @return 判断笔记的实际内容是否被修改
     */
    private boolean isModifyNote() {
        String noteTitle = (String) comboBoxNoteTitle.getSelectedItem();
        return !Objects.equals(noteTitle, note.getTitle())
                || !fieldPath.getText().equals(note.getSource())
                || rangeChanged();
    }

    private boolean rangeChanged() {
        String str = fieldRange.getText();
        Matcher matcher = rangePattern.matcher(str);
        int start = 0;
        int end = 0;
        if (matcher.matches()) {
            String[] ss = str.split(":");
            start = Integer.parseInt(ss[0].trim());
            end = Integer.parseInt(ss[1].trim());
        }
        return (start != note.getOffsetStart()) || (end != note.getOffsetEnd());
    }

}
