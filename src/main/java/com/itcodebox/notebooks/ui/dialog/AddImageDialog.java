package com.itcodebox.notebooks.ui.dialog;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.entity.ImageRecord;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.impl.NoteServiceImpl;
import com.itcodebox.notebooks.ui.tables.ImageTable;
import com.itcodebox.notebooks.utils.CustomUIUtil;
import com.itcodebox.notebooks.utils.ImageRecordUtil;
import com.itcodebox.notebooks.utils.StringUtil;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class AddImageDialog extends DialogWrapper {
    /**
     * 添加面板的图片最大尺寸
     */
    private final int IMAGE_MAX_SIZE = 200;
    private final TextFieldWithBrowseButton imagePathField = new TextFieldWithBrowseButton();
    private final JBTextField imageTitleField = new JBTextField();
    private static final int DEFAULT_WIDTH = 600;
    private static final int DEFAULT_HEIGHT = 360;
    private final JBTextArea imageDescTextArea = new JBTextArea();
    private final JLabel imageLabel = new JLabel();
    private final JBRadioButton radioButtonPNG = new JBRadioButton("PNG");
    private final JBRadioButton radioButtonJPG = new JBRadioButton("JPG");
    private final JBRadioButton radioButtonGIF = new JBRadioButton("GIF");
    private final Note note;
    private final ImageTable imageTable;
    private final Project project;
    private final JLabel sizeIconLabel = new JLabel("-1x-1", PluginIcons.ImageSize, JLabel.LEFT);
    private final JLabel lengthIconLabel = new JLabel("0 Byte", PluginIcons.Data, JLabel.LEFT);

    public AddImageDialog(Project project, Note note, ImageTable imageTable) {
        super(true);
        this.project = project;
        this.note = note;
        this.imageTable = imageTable;
        setTitle(message("addImageDialog.title"));


        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(radioButtonJPG);
        buttonGroup.add(radioButtonPNG);
        buttonGroup.add(radioButtonGIF);
        //截图PNG格式在MAC更清晰
        radioButtonPNG.setSelected(true);

        getRootPane().setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        //getRootPane().setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setOKButtonIcon(PluginIcons.Add);
        setOKButtonText(message("addImageDialog.button.ok"));
        setCancelButtonText(message("addImageDialog.button.cancel"));

        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String text = imagePathField.getText();
                if (CustomUIUtil.getImageLength(text) < 0) {
                    return;
                }

                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    new FullImageDialog(project,text).show();
                }
            }
        });
        imagePathField.addBrowseFolderListener(
                message("addImageDialog.choose.title"),
                null,
                project,
                new FileChooserDescriptor(true, false, false, false, false, false).withFileFilter(new Condition<VirtualFile>() {
                    @Override
                    public boolean value(VirtualFile virtualFile) {
                        String extension = virtualFile.getExtension();
                        extension = extension == null ? "" : extension.toLowerCase();
                        return PluginConstant.IMG_EXTENSION_LIST.contains(extension);
                    }
                })
        );
        JTextField field = imagePathField.getTextField();
        field.setBackground(JBColor.WHITE);
        field.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                String path = imagePathField.getText();
                if (CustomUIUtil.getImageLength(path) < 0) {
                    if (imageLabel.getIcon() != null) {
                        imageLabel.setIcon(PluginIcons.ImageDefaultBig);
                    }
                    sizeIconLabel.setText("-1x-1");
                    lengthIconLabel.setText("0 Byte");
                } else {
                    ImageIcon icon = new ImageIcon(path);
                    int width = icon.getIconWidth();
                    int height = icon.getIconHeight();
                    if (path.toLowerCase().endsWith(PluginConstant.GIF)) {
                        imageLabel.setIcon(icon);
                    } else {
                        Icon scaleImage = CustomUIUtil.scaleImageIcon(icon, IMAGE_MAX_SIZE);
                        imageLabel.setIcon(scaleImage == null ? PluginIcons.ImageDefaultBig : scaleImage);
                    }
                    File file = new File(path);
                    sizeIconLabel.setText(width + "x" + height);
                    lengthIconLabel.setText(StringUtil.formatFileSize(file.length()));
                }
            }
        });
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel middlePanel = new JPanel(new BorderLayout());
        JPanel topPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JLabel(message("addImageDialog.label.imagePath"), PluginIcons.Link, JLabel.LEFT), imagePathField)
                .addLabeledComponent(new JLabel(message("addImageDialog.label.imageTitle"), PluginIcons.ImageTitle, JLabel.LEFT), imageTitleField)
                .getPanel();
        middlePanel.add(topPanel, BorderLayout.NORTH);

        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.add(new JLabel(message("addImageDialog.label.imageDesc"), PluginIcons.Description, JLabel.LEFT), BorderLayout.NORTH);
        descPanel.add(new JBScrollPane(imageDescTextArea));
        middlePanel.add(descPanel);
        middlePanel.setBorder(BorderFactory.createEtchedBorder());
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(middlePanel);
        JPanel westPanel = new JPanel(new BorderLayout());
        imageLabel.setPreferredSize(new Dimension(IMAGE_MAX_SIZE, IMAGE_MAX_SIZE));
        imageLabel.setIcon(PluginIcons.ImageDefaultBig);
        imageLabel.setBackground(JBColor.WHITE);
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(sizeIconLabel);
        infoPanel.add(lengthIconLabel);
        westPanel.add(infoPanel, BorderLayout.NORTH);
        westPanel.add(new JBScrollPane(imageLabel));
        westPanel.setBorder(BorderFactory.createEtchedBorder());
        panel.add(westPanel, BorderLayout.WEST);

        JButton pasteBtn = new JButton(message("addImageDialog.button.paste"), PluginIcons.Paste);
        pasteBtn.addActionListener(e -> {
            //内存中的图片
            Image ramImage = CustomUIUtil.readFromClipboard();
            if (ramImage != null) {
                ImageIcon icon = new ImageIcon(ramImage);
                String extension = radioButtonPNG.isSelected() ? PluginConstant.PNG : (radioButtonJPG.isSelected() ? PluginConstant.JPG : PluginConstant.GIF);

                try {
                    //jpg好像没有PNG清晰
                    File imgTempFile = File.createTempFile(System.currentTimeMillis() + "", "." + extension, PluginConstant.TEMP_IMAGE_DIRECTORY_PATH.toFile());
                    //临时文件,记得退出
                    imgTempFile.deleteOnExit();
                    //把图片内容写入文件
                    BufferedImage destImage = new BufferedImage(ramImage.getWidth(null), ramImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
                    CustomUIUtil.writeImageToFile(ramImage, destImage, imgTempFile, extension);
                    //在这里刷新路径,因为此刻图片已经写完了, 这样才能正确的显示图片的大小等信息
                    imagePathField.setText(imgTempFile.getAbsolutePath());

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                imageLabel.setIcon(CustomUIUtil.scaleImageIcon(icon, IMAGE_MAX_SIZE));
            } else {
                Messages.showWarningDialog(message("addImageDialog.warning.message"), message("addImageDialog.warning.title"));
            }
        });

        JPanel pastePanel = new JPanel();
        pastePanel.add(radioButtonPNG);
        pastePanel.add(radioButtonJPG);
        pastePanel.add(radioButtonGIF);
        pastePanel.add(pasteBtn);
        westPanel.add(pastePanel, BorderLayout.SOUTH);
        return panel;
    }

    @Override
    protected void doOKAction() {
        File originFile = new File(imagePathField.getText());
        String name = originFile.getName();
        //随机字符串 + 包含带"."的拓展名
        String randomStr = StringUtil.random(32, true, true);
        String extensionWithPoint = "." + StringUtil.getExtension(name);
        String fileName = randomStr + extensionWithPoint;
        String thumbFileName = randomStr + PluginConstant.ThumbExtension + extensionWithPoint;
        try {
            //复制图片
            FileUtil.copy(originFile, PluginConstant.IMAGE_DIRECTORY_PATH.resolve(fileName).toFile());
            //创建缩略图
            CustomUIUtil.writeThumbImageToFile(originFile, PluginConstant.IMAGE_DIRECTORY_PATH.resolve(thumbFileName).toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageRecord imageRecord = new ImageRecord(imageTitleField.getText(), imageDescTextArea.getText(), fileName);
        List<ImageRecord> records = new ArrayList<>(imageTable.getListTableModel().getItems());
        records.add(imageRecord);
        //表格里的添加新数据
        imageTable.getListTableModel().addRow(imageRecord);
        //表格里选中新添加的数据
        int rowIndex = records.size() - 1;
        imageTable.getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
        //滚动到这一行
        imageTable.scrollRectToVisible(imageTable.getCellRect(rowIndex, 0, true));

        //数据库里保存新数据
        note.setImageRecords(ImageRecordUtil.convertToString(records));
        note.setUpdateTime(System.currentTimeMillis());
        NoteServiceImpl.getInstance().update(note);
        //通知其他Project ,Note已经修改
        ApplicationManager.getApplication().getMessageBus()
                .syncPublisher(RecordListener.TOPIC)
                .onNoteUpdated(project, new Note[]{note});
        super.doOKAction();
    }

    @Override
    protected Action[] createActions() {
        return new Action[]{getOKAction(), getCancelAction()};
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        String path = imagePathField.getText();
        if (path.trim().isEmpty()) {
            return new ValidationInfo(message("addImageDialog.validation.info.empty"), imagePathField);
        }
        if (CustomUIUtil.getImageLength(path) > PluginConstant.MAX_LENGTH) {
            return new ValidationInfo(message("addImageDialog.validation.info.length"), imagePathField);
        }
        if (CustomUIUtil.getImageLength(path) < 0) {
            return new ValidationInfo(message("addImageDialog.validation.info.path"), imagePathField);
        }
        return null;
    }
}
