package com.itcodebox.notebooks.ui.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.panels.VerticalBox;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class SupportDialog extends DialogWrapper {
    public SupportDialog() {
        super(true);
        setTitle(message("supportDialog.title"));
        setOKButtonText(message("supportDialog.button.ok.text"));
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        VerticalBox box = new VerticalBox();
        box.add(new JLabel(message("supportDialog.label.line1")));
        box.add(new JLabel(message("supportDialog.label.line2")));
        box.add(new JLabel(message("supportDialog.label.line3")));
        box.add(new JBLabel(PluginIcons.Donate));
        return box;
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{getOKAction()};
    }
}
