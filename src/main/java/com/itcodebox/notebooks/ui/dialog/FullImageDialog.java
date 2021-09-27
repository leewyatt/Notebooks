package com.itcodebox.notebooks.ui.dialog;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.HorizontalBox;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.utils.CustomUIUtil;
import com.itcodebox.notebooks.utils.StringUtil;
import icons.PluginIcons;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class FullImageDialog extends DialogWrapper {
    private final String fileName;
    private final Project project;
    private final File file;

    public FullImageDialog(Project project, String fileName) {
        super(true);
        this.project = project;
        setTitle(message("fullImageDialog.title"));
        this.fileName = fileName;
        this.file = PluginConstant.IMAGE_DIRECTORY_PATH.resolve(fileName).toFile();
        setOKButtonText(message("fullImageDialog.button.ok"));
        setAutoAdjustable(true);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        //中间图片区域
        ImageIcon icon = new ImageIcon(file.getAbsolutePath());
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();
        JLabel imageLabel = new JLabel(width == -1 ? PluginIcons.ImageDefaultBig : icon);
        imageLabel.setBackground(JBColor.WHITE);
        panel.add(new JBScrollPane(imageLabel));

        //顶部信息区域
        HorizontalBox topBox = new HorizontalBox();
        //size
        topBox.add(Box.createHorizontalStrut(10));
        topBox.add(new JLabel(width + "x" + height, PluginIcons.ImageSize, JLabel.LEFT));
        //length
        topBox.add(Box.createHorizontalStrut(10));
        topBox.add(new JLabel(StringUtil.formatFileSize(file.length()), PluginIcons.Data, JLabel.LEFT));
        //type
        topBox.add(Box.createHorizontalStrut(10));
        topBox.add(new JLabel(StringUtil.getExtension(fileName), PluginIcons.Image, JLabel.LEFT));
        //time
        topBox.add(Box.createHorizontalStrut(10));
        topBox.add(new JLabel(CustomUIUtil.convertTimeToString(file.lastModified()), PluginIcons.Time, JLabel.LEFT));
        topBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), message("fullImageDialog.image.info")));
        panel.add(topBox, BorderLayout.NORTH);

        return panel;
    }

    @Override
    protected Action[] createActions() {
        DialogWrapperAction copyToClipboardAction = new DialogWrapperAction(message("fullImageDialog.action.copy")) {
            @Override
            protected void doAction(ActionEvent e) {
                if (!file.exists()) {
                    // 提示文件不存在?
                    return;
                }
                try {
                    CustomUIUtil.writeToClipboard(ImageIO.read(file));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

        };
        copyToClipboardAction.putValue(Action.SMALL_ICON, PluginIcons.Paste);

        DialogWrapperAction saveFileAction = new DialogWrapperAction(message("fullImageDialog.action.export")) {
            @Override
            protected void doAction(ActionEvent e) {
                if (!file.exists()) {
                    // 提示文件不存在?
                    return;
                }

                //3. IO输出
                VirtualFileWrapper targetFileWrapper = FileChooserFactory.getInstance().createSaveFileDialog(
                        new FileSaverDescriptor(message("fullImageDialog.fileChooser.title")
                                , "", StringUtil.getExtension(file.getName())), project).save((VirtualFile) null, file.getName());
                //情况2: 用户没有选择文件或者文件夹, 返回
                if (targetFileWrapper == null) {
                    return;
                }
                VirtualFile targetFile = targetFileWrapper.getVirtualFile(true);
                if (targetFile != null && targetFile.getCanonicalPath() != null) {
                    try {
                        FileUtil.copy(file, new File(targetFile.getCanonicalPath()));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        };
        saveFileAction.putValue(Action.SMALL_ICON, AllIcons.Actions.MenuSaveall);

        return new Action[]{copyToClipboardAction, saveFileAction, getOKAction()};
    }

}
