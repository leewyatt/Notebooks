package com.itcodebox.notebooks.ui.panes;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.panels.HorizontalBox;
import com.intellij.util.ui.*;
import com.itcodebox.notebooks.constant.PluginColors;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.entity.ImageRecord;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.impl.NoteServiceImpl;
import com.itcodebox.notebooks.ui.dialog.AddImageDialog;
import com.itcodebox.notebooks.ui.dialog.FullImageDialog;
import com.itcodebox.notebooks.ui.tables.ImageTable;
import com.itcodebox.notebooks.ui.tables.ImageTableCellRenderer;
import com.itcodebox.notebooks.utils.CustomFileUtil;
import com.itcodebox.notebooks.utils.CustomUIUtil;
import com.itcodebox.notebooks.utils.ImageRecordUtil;
import com.itcodebox.notebooks.utils.StringUtil;
import icons.PluginIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class ImagePanel extends JPanel {
    private Note note;
    private final Project project;
    private final JLabel imageLabel = new JLabel();
    private final JBTextField imageTitleField = new JBTextField();
    private final JBTextArea imageDescTextArea = new JBTextArea();
    private final ImageTable imageTable;
    private final JLabel sizeIconLabel = new JLabel("-1x-1", PluginIcons.ImageSize, JLabel.LEFT);
    private final JLabel lengthIconLabel = new JLabel("0 Byte", PluginIcons.Data, JLabel.LEFT);

    public JBTextField getImageTitleField() {
        return imageTitleField;
    }

    public JBTextArea getImageDescTextArea() {
        return imageDescTextArea;
    }

    public ImageTable getImageTable() {
        return imageTable;
    }

    public ImagePanel(Project project) {
        super(true);
        this.project = project;
        imageTable = new ImageTable(project);
        imageDescTextArea.setLineWrap(true);
        JBFont font = JBUI.Fonts.label();
        imageTitleField.setFont(font.biggerOn(1));
        imageDescTextArea.setFont(font);

        setLayout(new BorderLayout());
        add(createCenterPanel());

    }

    public void setNote(Note note) {
        if (note == null) {
            this.note = null;
            imageTable.getListTableModel().setItems(new ArrayList<>());
            return;
        }
        String oldImageRecords = ImageRecordUtil.convertToString(imageTable.getListTableModel().getItems());

        //如果是同一个note 并且ImageRecords不变, 那么不刷新
        if (this.note != null && Objects.equals(this.note.getId(), note.getId()) && Objects.equals(oldImageRecords, this.note.getImageRecords())) {
            this.note = note;
            return;
        }

        this.note = note;
        ImageRecord selectedImageRecord = imageTable.getSelectedObject();
        List<ImageRecord> records = ImageRecordUtil.convertToList(note.getImageRecords());
        imageTable.getListTableModel().setItems(records);
        int index = selectedImageRecord == null ? -1 : imageTable.getListTableModel().indexOf(selectedImageRecord);
        if (records.size() > 0) {
            //恢复选择
            imageTable.getSelectionModel().setSelectionInterval(index == -1 ? 0 : index, 0);
        }
    }

    private JComponent createCenterPanel() {
        JPanel detailPanel = new JPanel(new BorderLayout());
        JBSplitter contentPanel = new JBSplitter(true, 0.745F);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(JBColor.WHITE);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ImageRecord imageRecord = imageTable.getSelectedObject();
                if (imageRecord == null || imageRecord.getImagePath() == null || imageRecord.getImagePath().trim().isEmpty()) {
                    return;
                }
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    new FullImageDialog(project, imageRecord.getImagePath()).show();
                }
            }
        });
        panel.add(imageLabel);
        contentPanel.setFirstComponent(new JBScrollPane(panel));
        JLabel tipLabel = new JLabel(message("imagePanel.label.tip"));
        tipLabel.setForeground(PluginColors.TEXT_TITLE);
        JPanel textPanel = FormBuilder.createFormBuilder()
                .addComponent(tipLabel)
                .addLabeledComponent(new JLabel(message("imagePanel.label.imageTitle"), PluginIcons.Rename, JLabel.LEFT), imageTitleField)
                .addComponent(new JLabel(message("imagePanel.label.imageDesc"), PluginIcons.Description, JLabel.LEFT))
                .addComponent(new JBScrollPane(imageDescTextArea, JBScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)).getPanel();
        textPanel.setBorder(BorderFactory.createEtchedBorder());
        contentPanel.setSecondComponent(textPanel);


        sizeIconLabel.setForeground(PluginColors.TEXT_TITLE);
        lengthIconLabel.setForeground(PluginColors.TEXT_TITLE);
        HorizontalBox topBox = new HorizontalBox();
        topBox.add(new JLabel(message("imagePanel.label.thumbInfo")));
        topBox.add(Box.createHorizontalStrut(5));
        topBox.add(sizeIconLabel);
        topBox.add(Box.createHorizontalStrut(10));
        topBox.add(lengthIconLabel);
        topBox.add(Box.createHorizontalStrut(10));
        topBox.setBorder(BorderFactory.createEtchedBorder());

        detailPanel.add(topBox, BorderLayout.NORTH);
        detailPanel.add(contentPanel);
        detailPanel.setBorder(new JBEmptyBorder(0, 0, 3, 0));
        JBSplitter centerPanel = new JBSplitter(false, 0.3F);
        centerPanel.setFirstComponent(createImagePanel());
        centerPanel.setSecondComponent(detailPanel);
        return centerPanel;
    }

    private JPanel createImagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        settingImageTable();

        panel.add(new JBScrollPane(imageTable));

        JButton addImageBtn = new JButton(message("imagePanel.button.add"), AllIcons.General.Add);
        addImageBtn.addActionListener(e -> {
            if (note == null) {
                Messages.showWarningDialog(project, message("imagePanel.error.message"), message("imagePanel.error.title"));
            } else {
                new AddImageDialog(project, note, imageTable).show();
            }
        });

        panel.add(addImageBtn, BorderLayout.SOUTH);
        return panel;
    }

    private void settingImageTable() {
        //设置model
        imageTable.setModelAndUpdateColumns(new ListTableModel<ImageRecord>(new ColumnInfo<ImageRecord, ImageRecord>(message("imagePanel.columnInfo.image")) {
            final ImageTableCellRenderer imageTableCellRenderer = new ImageTableCellRenderer();

            @Nullable
            @Override
            public ImageRecord valueOf(ImageRecord o) {
                return o;
            }

            @Override
            public @Nullable TableCellRenderer getRenderer(ImageRecord imageRecord) {
                return imageRecord == null ? null : imageTableCellRenderer;
            }
        }));
        //设置改变时候的响应
        imageTable.getSelectionModel().addListSelectionListener(e -> {
            ImageRecord imageRecord = imageTable.getSelectedObject();
            if (imageRecord == null) {
                imageLabel.setIcon(PluginIcons.ImageDefaultBig);
                imageTitleField.setText("");
                imageDescTextArea.setText("");
                sizeIconLabel.setText("-1x-1");
                lengthIconLabel.setText("0 Byte");
                return;
            }
            File thumbFile = CustomUIUtil.getThumbFile(imageRecord.getImagePath());
            ImageIcon icon = new ImageIcon(thumbFile.getAbsolutePath());

            int width = icon.getIconWidth();
            int height = icon.getIconHeight();
            if (width == -1) {
                imageLabel.setIcon(PluginIcons.ImageDefaultBig);
            } else {
                imageLabel.setIcon(icon);
            }

            imageTitleField.setText(imageRecord.getImageTitle());
            imageTitleField.setCaretPosition(0);
            imageDescTextArea.setText(imageRecord.getImageDesc());
            imageDescTextArea.setCaretPosition(0);
            sizeIconLabel.setText(width + "x" + height);
            if (thumbFile.exists() && thumbFile.isFile()) {
                lengthIconLabel.setText(StringUtil.formatFileSize(thumbFile.length()));
            } else {
                lengthIconLabel.setText("0 Byte");
            }
        });

        imageTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (note != null && SwingUtilities.isRightMouseButton(e) && !e.isConsumed() && imageTable.getSelectedObject() != null) {
                    createPopupMenu().show(imageTable, e.getX(), e.getY());
                    e.consume();
                }
            }
        });
    }

    private JBPopupMenu createPopupMenu() {
        JBPopupMenu popupMenu = new JBPopupMenu();
        JBMenuItem menuItemOpenFullImage = new JBMenuItem(message("imagePanel.menuItem.originalImage"), PluginIcons.ImageColorful);
        menuItemOpenFullImage.addActionListener(e -> {
            ImageRecord imageRecord = imageTable.getSelectedObject();
            if (imageRecord == null || imageRecord.getImagePath() == null) {
                return;
            }
            new FullImageDialog(project, imageRecord.getImagePath()).show();
        });
        popupMenu.add(menuItemOpenFullImage);

        popupMenu.add(new JSeparator());
        JBMenuItem menuItemDelete = new JBMenuItem(message("imagePanel.menuItem.remove"), PluginIcons.Delete);
        menuItemDelete.addActionListener(e -> {
            ImageRecord imageRecord = imageTable.getSelectedObject();
            if (imageRecord == null) {
                return;
            }
            ListTableModel<ImageRecord> listTableModel = imageTable.getListTableModel();
            int index = listTableModel.indexOf(imageRecord);
            if (index == -1) {
                return;
            }
            int result = Messages.showOkCancelDialog(
                    message("imagePanel.removeDialog.message"),
                    message("imagePanel.removeDialog.title"),
                    message("imagePanel.removeDialog.ok"),
                    message("imagePanel.removeDialog.cancel"), Messages.getQuestionIcon());
            if (result != Messages.OK) {
                return;
            }
            //从表格里进行了删除
            listTableModel.removeRow(index);
            //从数据库里进行删除与更新
            List<ImageRecord> items = listTableModel.getItems();
            note.setImageRecords(ImageRecordUtil.convertToString(items));
            note.setUpdateTime(System.currentTimeMillis());
            NoteServiceImpl.getInstance().update(note);
            ApplicationManager.getApplication().getMessageBus()
                    .syncPublisher(RecordListener.TOPIC)
                    .onNoteUpdated(project, new Note[]{note});
            //最后还需要从图片库里进行删除
            try {
                CustomFileUtil.deleteImagesAndThumb(imageRecord.getImagePath());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        popupMenu.add(menuItemDelete);
        return popupMenu;
    }

    private ImageIcon getIconFromPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        ImageIcon icon = null;
        try {
            icon = new ImageIcon(PluginConstant.IMAGE_DIRECTORY_PATH.resolve(path).toUri().toURL());
        } catch (MalformedURLException malformedURLException) {
            malformedURLException.printStackTrace();
        }
        return icon;
    }

}
