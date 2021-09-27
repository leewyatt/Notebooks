package com.itcodebox.notebooks.ui.dialog;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBLabel;
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
public class AddNotebookDialog extends DialogWrapper {
    private static final int DEFAULT_WIDTH = 380;
    private static final int DEFAULT_HEIGHT = 130;
    private final DefaultComboBoxModel<String>
            notebookTitleModel = new DefaultComboBoxModel<>();
    private final ComboBox<String> comboBoxNotebookTitle = new ComboBox<>(notebookTitleModel);
    private final NotebookService notebookService = NotebookServiceImpl.getInstance();
    private final Project project;
    public AddNotebookDialog(Project project) {
        super(true);
        this.project = project;
        setTitle(message("addNotebookDialog.title"));
        setOKButtonText(message("addNotebookDialog.button.ok"));
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
        Notebook notebook = notebookService.insert(new Notebook((String) comboBoxNotebookTitle.getSelectedItem(), System.currentTimeMillis()));
        ApplicationManager.getApplication().getMessageBus().syncPublisher(RecordListener.TOPIC)
                .onNotebookAdd(project,notebook,true,false);
        super.doOKAction();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        comboBoxNotebookTitle.setEditable(true);
        notebookTitleModel.addAll(notebookService.getTitles());
        JPanel panel = new JPanel(new MigLayout(new LC().fill().gridGap("0!", "0!").insets("0")));
        panel.add(new JBLabel(message("addNotebookDialog.label.notebookTitle"), PluginIcons.NotebookCell,JBLabel.LEFT));
        panel.add(comboBoxNotebookTitle,new CC().growX().push().wrap());
        return panel;
    }

    //当对话框显示时, 让指定组件带有焦点
    //@Override
    //public @Nullable JComponent getPreferredFocusedComponent() {
    //    return comboBoxNotebookTitle;
    //}

    @Override
    protected @Nullable ValidationInfo doValidate() {
        String notebookTitle = CustomUIUtil.getComboBoxText(comboBoxNotebookTitle);
        if (StringUtil.isEmptyOrNull(notebookTitle)) {
           return new ValidationInfo(message("addNoteDialog.validate.message"), comboBoxNotebookTitle);
        } else if (notebookService.findByTitle(notebookTitle)!=null) {
            return new ValidationInfo(message("addNotebookDialog.validate.titleConflict"), comboBoxNotebookTitle);
        }
        return null;
    }
}
