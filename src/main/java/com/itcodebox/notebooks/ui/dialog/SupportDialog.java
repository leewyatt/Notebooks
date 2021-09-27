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
        box.add(new JLabel("使用支付宝/微信支付打赏后请留言提供您的名字/昵称"));
        box.add(new JLabel("您提供的名字/昵称以及打赏总额将会被记录,用于展示在打赏列表里(例如QQ群)。"));
        box.add(new JLabel("感谢您的慷慨打赏！"));
        box.add(new JBLabel(PluginIcons.Donate));
        return box;
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{getOKAction()};
    }
}
