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
import java.util.regex.Matcher;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

public class AddNoteDialog extends BaseNoteDialog {
    public AddNoteDialog(Project project, Note note) {
        super(project, note);
        setTitle(message("addNoteDialog.title"));
        setOKButtonText(message("addNoteDialog.button.addNote"));
        setOKButtonIcon(PluginIcons.Add);
        setCancelButtonText(message("addNoteDialog.button.cancel"));
    }

    /**
     * 1. windows 平台下本来也就是 确定/取消按钮.
     * 2. macOS平台会多出一个Help按钮.
     * 3. 不同平台下,按钮的顺序可能不一致
     * 为了使UI统一按钮数量和顺序 ,还是简单的重写下createActions方法;
     * 注意: 这里就不要重写createSouthPanel了,会覆盖掉这些默认按钮的.
     *
     * @return 创建Actions 替换默认的 按钮
     */
    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{getHelpAction(),getOKAction(), getCancelAction()};
    }



    /**
     * 点击Ok按钮时执行的动作
     */
    @Override
    protected void doOKAction() {
        setNoteProperty();
        note = noteService.insert(note);
        ApplicationManager
                .getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onNoteAdd(project, note);
        // 调用父类的okAction去关闭
        super.doOKAction();
    }

    //@Override
    //public @Nullable
    //JComponent getPreferredFocusedComponent() {
    //    return focusedComponent;
    //}

    private void setNoteProperty() {
        note.setTitle((String) comboBoxNoteTitle.getSelectedItem());
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
            MainPanel mainPanel = project.getService(NotebooksUIManager.class).getMainPanel();
            if (mainPanel != null) {
                mainPanel.getNotebookTable().selectedRow(notebook);
            }
        }

        String chapterName = (String) comboBoxChapterTitle.getSelectedItem();
        Chapter chapter = chapterService.findByTitle(chapterName, notebook.getId());
        if (chapter == null) {
            chapter = chapterService.insert(new Chapter(notebook.getId(), chapterName, System.currentTimeMillis()));
            ApplicationManager
                    .getApplication()
                    .getMessageBus()
                    .syncPublisher(RecordListener.TOPIC)
                    .onChapterAdd(project, chapter, true, false);
        } else {
            MainPanel mainPanel = project.getService(NotebooksUIManager.class).getMainPanel();
            if (mainPanel != null) {
                mainPanel.getChapterTable().selectedRow(chapter);
            }
        }
        note.setNotebookId(notebook.getId());
        note.setChapterId(chapter.getId());
        long time = System.currentTimeMillis();
        note.setCreateTime(time);
        note.setUpdateTime(time);
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
    }

    @Override
    protected @NotNull
    List<ValidationInfo> doValidateAll() {
        ArrayList<ValidationInfo> infos = new ArrayList<>();
        String notebookTitle = CustomUIUtil.getComboBoxText(comboBoxNotebookTitle);
        if (StringUtil.isEmptyOrNull(notebookTitle)) {
            infos.add(new ValidationInfo(message("addNoteDialog.validate.message"), comboBoxNotebookTitle));
        }
        String chapterTitle =  CustomUIUtil.getComboBoxText(comboBoxChapterTitle);
        if (StringUtil.isEmptyOrNull(chapterTitle)) {
            infos.add(new ValidationInfo(message("addNoteDialog.validate.message"), comboBoxChapterTitle));
        }
        String noteTitle = CustomUIUtil.getComboBoxText(comboBoxNoteTitle);
        if (StringUtil.isEmptyOrNull(noteTitle)) {
            infos.add(new ValidationInfo(message("addNoteDialog.validate.message"), comboBoxNoteTitle));
        } else if (!StringUtil.isEmptyOrNull(notebookTitle)&&!StringUtil.isEmptyOrNull(chapterTitle)) {
            Note note = noteService.findByTitles(noteTitle, chapterTitle, notebookTitle);
            if (note != null) {
                infos.add(new ValidationInfo(message("addNoteDialog.validate.noteTitleConflict"), comboBoxNoteTitle));
            }
        }
        Matcher matcher = rangePattern.matcher(fieldRange.getText());
        if (!matcher.matches()) {
            infos.add(new ValidationInfo(message("addNoteDialog.validate.rangeError")+RANGE_REGEX, fieldRange));
        }
        return infos;
    }


}
