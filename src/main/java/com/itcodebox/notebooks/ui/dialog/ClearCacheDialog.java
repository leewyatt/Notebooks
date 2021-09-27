package com.itcodebox.notebooks.ui.dialog;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBEmptyBorder;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.ui.notify.NotifyUtil;
import com.itcodebox.notebooks.utils.CustomFileUtil;
import com.itcodebox.notebooks.utils.StringUtil;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class ClearCacheDialog extends DialogWrapper {
    private static final int DEFAULT_WIDTH = 350;
    private static final int DEFAULT_HEIGHT = 160;
    private final Project project;

    public ClearCacheDialog(Project project) {
        super(true);
        this.project = project;
        setTitle(message("cacheDialog.title"));
        setOKButtonText(message("cacheDialog.button.ok"));
        setCancelButtonText(message("button.close"));
        getRootPane().setMinimumSize(new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT));
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        File thumbDir = PluginConstant.TEMP_IMAGE_DIRECTORY_PATH.toFile();
        //统计缓存大小
        JLabel sizeLabel = new JLabel();
        sizeLabel.setText(StringUtil.formatFileSize(CustomFileUtil.sizeOfDirectory(thumbDir)));
        //统计缓存文件数量
        JLabel amountLabel = new JLabel();
        amountLabel.setText(getFileAmount(thumbDir));

        JPanel panel = FormBuilder
                .createFormBuilder()
                .addLabeledComponent(new JLabel(message("cacheDialog.label.size"), PluginIcons.Data, JLabel.LEFT), sizeLabel)
                .addLabeledComponent(new JLabel(message("cacheDialog.label.amount"), AllIcons.Actions.GroupByPrefix, JLabel.LEFT), amountLabel)
                .getPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),new JBEmptyBorder(0,5,0,5)));
        //panel.setBackground(JBColor.WHITE);
        return panel;
    }

    private String getFileAmount( File thumbDir) {
        if (!thumbDir.exists()) {
            return "0";
        }
        File[] files = thumbDir.listFiles();
        if (files == null || files.length == 0) {
            return "0";
        }
        return String.valueOf(files.length);
    }


    @Override
    protected void doOKAction() {
        File thumbDir = PluginConstant.TEMP_IMAGE_DIRECTORY_PATH.toFile();
        if (!thumbDir.exists()) {
            super.doOKAction();
            return;
        }
        File[] files = thumbDir.listFiles();
        if (files == null || files.length == 0) {
            super.doOKAction();
            return;
        }
        int len = files.length;
        ProgressManager.getInstance().run(new Task.Backgroundable(project, message("cacheDialog.background.title"), false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                PluginConstant.IsClearing.set(true);
                for (int i = 0; i < len; i++) {
                    indicator.setText(message("cacheDialog.background.indicator.text")+" "+i+"/"+len);
                    files[i].delete();
                }
                PluginConstant.IsClearing.set(false);
                NotifyUtil.showInfoNotification(project,PluginConstant.NOTIFICATION_CLEAR_CACHE,message("cacheDialog.notify.title"),message("cacheDialog.notify.message"));
            }
        });

        super.doOKAction();
    }

    @Override
    protected Action [] createActions() {
        return new Action[]{getOKAction(), getCancelAction()};
    }
}
