package com.itcodebox.notebooks.ui.dialog;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.components.JBTextArea;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class ImportErrorDialog extends DialogWrapper {
    private final String line = System.lineSeparator();

    private static final int DEFAULT_WIDTH = 620;
    private static final int DEFAULT_HEIGHT = 490;
    private final String errorMessage;
    public ImportErrorDialog(String errorMessage) {
        super(true);
        this.errorMessage = errorMessage;
        setTitle(message("importErrorDialog.title"));
        setCancelButtonText(message("button.close"));
        setOKButtonText(message("importErrorDialog.button.ok"));
        setOKButtonIcon(AllIcons.Nodes.PluginJB);
        getRootPane().setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        //getRootPane().setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        init();
    }

    @Override
    protected void doOKAction() {
        BrowserUtil.browse("https://plugins.jetbrains.com/plugin/16998-notebook/versions");
        super.doOKAction();
    }

    @Override
    protected Action[] createActions() {
        return new Action[]{
                getOKAction(),getCancelAction()};
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JBTextArea reasonTextArea = new JBTextArea();
        reasonTextArea.setLineWrap(true);
        reasonTextArea.setText(
                message("importErrorDialog.reason.msg1") + line +line+
                message("importErrorDialog.reason.msg2")+ line +line+
                message("importErrorDialog.reason.msg3") + line +
                message("importErrorDialog.reason.msg4") +line +
                message("importErrorDialog.reason.msg5") +line +
                message("importErrorDialog.reason.msg6") +line +line+
                message("importErrorDialog.reason.msg7")+line +
                message("importErrorDialog.reason.msg8"));

        JBTextArea errorTextArea = new JBTextArea();
        errorTextArea.setLineWrap(true);
        errorTextArea.setText(errorMessage);
        errorTextArea.setCaretPosition(0);
        JBTabbedPane tabbedPane = new JBTabbedPane();
        tabbedPane.addTab(message("importErrorDialog.tab.reason.title"), new JBScrollPane(reasonTextArea));
        tabbedPane.addTab(message("importErrorDialog.tab.exception.title"), new JBScrollPane(errorTextArea));
        return tabbedPane;
    }
}
