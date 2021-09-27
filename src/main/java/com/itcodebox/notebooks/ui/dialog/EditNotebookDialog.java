package com.itcodebox.notebooks.ui.dialog;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.itcodebox.notebooks.entity.Notebook;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.NotebookService;
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

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class EditNotebookDialog extends DialogWrapper {
    private static final int DEFAULT_WIDTH = 380;
    private static final int DEFAULT_HEIGHT = 190;
    private final DefaultComboBoxModel<String>
            notebookTitleModel = new DefaultComboBoxModel<>();
    private final ComboBox<String> comboBoxNotebookTitle = new ComboBox<>(notebookTitleModel);
    private final NotebookService notebookService = NotebookServiceImpl.getInstance();
    private final Project project;
    private final String notebookTitle;

    public EditNotebookDialog(Project project, String  notebookTitle) {
        super(true);
        this.project = project;
        this.notebookTitle = notebookTitle;
        setTitle(message("editNotebookDialog.title"));
        setOKButtonText(message("editNotebookDialog.button.ok"));
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
        String newNotebookTitle = CustomUIUtil.getComboBoxText(comboBoxNotebookTitle);
        if (!notebookTitle.equals(newNotebookTitle)) {
            Notebook notebook = notebookService.findByTitle(notebookTitle);
            notebook.setTitle(newNotebookTitle);
            //一. 数据库里修改
            notebookService.update(notebook);
            //二. 通知UI修改
            ApplicationManager
                    .getApplication()
                    .getMessageBus()
                    .syncPublisher(RecordListener.TOPIC)
                    .onNotebookTitleUpdated(project, notebook);
        }
        super.doOKAction();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        comboBoxNotebookTitle.setEditable(true);
        notebookTitleModel.addAll(notebookService.getTitles());
        notebookTitleModel.setSelectedItem(notebookTitle);
        JPanel panel = new JPanel(new MigLayout(new LC().fill().gridGap("0!", "0!").insets("0")));
        panel.add(new JBLabel(message("editNotebookDialog.label.oldTitle"), PluginIcons.NotebookCell,JBLabel.LEFT));
        JBTextField textField = new JBTextField(notebookTitle);
        textField.setEditable(false);
        panel.add(textField,new CC().growX().push().wrap());
        panel.add(new JBLabel(message("editNotebookDialog.label.newTitle"), PluginIcons.NotebookCell,JBLabel.LEFT));
        panel.add(comboBoxNotebookTitle,new CC().growX().push().wrap());
        return panel;
    }

    //@Override
    //public @Nullable JComponent getPreferredFocusedComponent() {
    //    return comboBoxNotebookTitle;
    //}

    @Override
    protected @Nullable ValidationInfo doValidate() {
        String notebookTitleTemp = CustomUIUtil.getComboBoxText(comboBoxNotebookTitle);
        if (StringUtil.isEmptyOrNull(notebookTitleTemp)) {
           return new ValidationInfo(message("addNoteDialog.validate.message"), comboBoxNotebookTitle);
        } else if (!notebookTitleTemp.equals(notebookTitle)&&notebookService.findByTitle(notebookTitleTemp)!=null) {
            return new ValidationInfo(message("addNotebookDialog.validate.titleConflict"), comboBoxNotebookTitle);
        }
        return null;
    }
}
