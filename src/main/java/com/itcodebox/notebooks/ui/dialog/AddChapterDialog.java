package com.itcodebox.notebooks.ui.dialog;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBLabel;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.entity.Notebook;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.ChapterService;
import com.itcodebox.notebooks.service.NotebookService;
import com.itcodebox.notebooks.service.impl.ChapterServiceImpl;
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
public class AddChapterDialog extends DialogWrapper {
    private static final int DEFAULT_WIDTH = 380;
    private static final int DEFAULT_HEIGHT = 190;
    private final DefaultComboBoxModel<String>
            notebookTitleModel = new DefaultComboBoxModel<>();
    private final ComboBox<String> comboBoxNotebookTitle = new ComboBox<>(notebookTitleModel);
    protected DefaultComboBoxModel<String>
            chapterTitleModel = new DefaultComboBoxModel<>();
    protected ComboBox<String> comboBoxChapterTitle = new ComboBox<>(chapterTitleModel);
    private final NotebookService notebookService = NotebookServiceImpl.getInstance();
    protected ChapterServiceImpl chapterService = ChapterServiceImpl.getInstance();
    private final Project project;
    private final String notebookTitle;

    public AddChapterDialog(Project project, String notebookTitle) {
        super(true);
        this.project = project;
        this.notebookTitle = notebookTitle;
        setTitle(message("addChapterDialog.title"));
        setOKButtonText(message("addChapterDialog.button.ok"));
        setOKButtonIcon(PluginIcons.Add);
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
        String selectedNotebookTitle = (String) comboBoxNotebookTitle.getSelectedItem();
        String selectedChapterTitle = (String) comboBoxChapterTitle.getSelectedItem();
        long time = System.currentTimeMillis();
        Notebook notebook = notebookService.findByTitle(selectedNotebookTitle);
        // 如果没有选择任何的Notebook 那么就创建一个默认的
        if (notebook == null) {
            NotebookService notebookService =NotebookServiceImpl.getInstance();
            notebook = notebookService.insert(new Notebook(selectedNotebookTitle, time));
            ApplicationManager
                    .getApplication()
                    .getMessageBus()
                    .syncPublisher(RecordListener.TOPIC)
                    .onNotebookAdd(project,notebook, true, false);
        }
        // 再根据Notebook来插入一个新的chapter
        Integer bookId = notebook.getId();
        ChapterService chapterService = ChapterServiceImpl.getInstance();
        Chapter chapter = chapterService.insert(new Chapter(bookId, selectedChapterTitle, time));
        ApplicationManager
                .getApplication()
                .getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onChapterAdd(project,chapter, true, false);
        super.doOKAction();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        comboBoxNotebookTitle.setEditable(true);
        notebookTitleModel.addAll(notebookService.getTitles());
        comboBoxChapterTitle.setEditable(true);
        comboBoxNotebookTitle.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                chapterTitleModel.removeAllElements();
                chapterTitleModel.addAll(chapterService.getTitles((String) comboBoxNotebookTitle.getSelectedItem()));
            }
        });
        JPanel panel = new JPanel(new MigLayout(new LC().fill().gridGap("0!", "0!").insets("0")));
        panel.add(new JBLabel(message("addNoteDialog.label.notebookTitle"), PluginIcons.NotebookCell, JBLabel.LEFT));
        panel.add(comboBoxNotebookTitle, new CC().growX().push().wrap());
        panel.add(new JBLabel(message("addNoteDialog.label.chapterTitle"), PluginIcons.ChapterCell, JBLabel.LEFT));
        panel.add(comboBoxChapterTitle, new CC().growX().push().wrap());

        if (!StringUtil.isEmptyOrNull(notebookTitle)) {
            notebookTitleModel.setSelectedItem(notebookTitle);
        }
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
        if (StringUtil.isEmptyOrNull(notebookTitleTemp)) {
            infos.add(new ValidationInfo(message("addNoteDialog.validate.message"), comboBoxNotebookTitle));
        }
        String chapterTitle = CustomUIUtil.getComboBoxText(comboBoxChapterTitle);
        if (StringUtil.isEmptyOrNull(chapterTitle)) {
            infos.add(new ValidationInfo(message("addNoteDialog.validate.message"), comboBoxChapterTitle));
        } else if (!StringUtil.isEmptyOrNull(notebookTitleTemp)) {
            Notebook notebookTemp = notebookService.findByTitle(notebookTitleTemp);
            if (notebookTemp != null && chapterService.findByTitle(chapterTitle, notebookTemp.getId()) != null) {
                infos.add(new ValidationInfo(message("addChapterDialog.validate.titleConflict"), comboBoxChapterTitle));
            }
        }
        return infos;
    }
}
