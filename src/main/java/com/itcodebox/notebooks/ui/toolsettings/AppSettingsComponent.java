package com.itcodebox.notebooks.ui.toolsettings;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.FontComboBox;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.ui.component.TitledPanel;
import com.itcodebox.notebooks.ui.dialog.SupportDialog;
import com.itcodebox.notebooks.utils.CustomUIUtil;
import icons.PluginIcons;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.apache.http.client.utils.URIBuilder;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class AppSettingsComponent {

    private final JPanel settingsPanel;

    private final FontComboBox fontComboBox = new FontComboBox();
    private final DefaultComboBoxModel<Integer> fontSizeModel = new DefaultComboBoxModel<Integer>();
    private final ComboBox<Integer> fontSizeBox = new ComboBox<Integer>(fontSizeModel);
    private final JBCheckBox restoreSelectedCheckBox = new JBCheckBox();
    private final JBCheckBox addMenuItemVisibleCheckBox = new JBCheckBox();
    private final JBCheckBox itemExpandableCheckBox = new JBCheckBox();
    private final JBCheckBox showFocusBorderCheckBox = new JBCheckBox();
    private final JBTextArea markdownTemplateArea = new JBTextArea();
    private final IntegerField thumbMaxSizeField = new IntegerField(message("settingPanel.thumbnail.maxSize"), PluginConstant.ThumbSizeMini,PluginConstant.ThumbSizeMax);
    private final AtomicBoolean isClearing= new AtomicBoolean(false);

    public AppSettingsComponent() {
        fontSizeModel.addAll(Stream.iterate(8, item -> item + 2).limit(33).collect(Collectors.toList()));
        //List<Integer> list = Stream.iterate(8, item -> item + 2).limit(33).collect(Collectors.toList());
        //for (Integer integer : list) {
        //    fontSizeModel.addElement(integer);
        //}
        fontSizeBox.setEditable(true);

        restoreSelectedCheckBox.setText(message("settingPanel.checkbox.restore"));
        addMenuItemVisibleCheckBox.setText(message("settingPanel.checkbox.showAddItem"));
        itemExpandableCheckBox.setText(message("settingPanel.checkbox.expandableItems"));
        showFocusBorderCheckBox.setText(message("settingPanel.checkbox.showFocusBorder"));

        markdownTemplateArea.setFont(JBFont.create(markdownTemplateArea.getFont()).biggerOn(2));

        settingsPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new MigLayout(new LC().flowY().fill().gridGap("0!", "0!").insets("0")));
        // 字体设置
        topPanel.add(new TitledPanel(message("settingPanel.fontSettings.title")) {
            @Override
            protected void addComponentToContentPane(JPanel contentPane) {
                contentPane.add(new JBLabel(message("settingPanel.fontSettings.fontName")));
                contentPane.add(fontComboBox, "gapright 6");
                contentPane.add(new JBLabel(message("settingPanel.fontSettings.fontSize")));
                contentPane.add(fontSizeBox, "wrap 8");
                contentPane.add(new JLabel(message("settingPanel.fontSettings.default")), new CC().spanX(2).split(2));
                JButton defaultSettings = new JButton(message("settingPanel.fontSettings.button.text"));
                defaultSettings.addActionListener(e -> {
                    JBFont font = CustomUIUtil.getMyDefaultFont();
                    fontComboBox.setFontName(font.getFontName());
                    fontSizeBox.setSelectedItem(font.getSize());
                });
                contentPane.add(defaultSettings, new CC().wrap("8"));
                JBLabel label = new JBLabel(message("settingPanel.fontSettings.description"));
                label.setForeground(JBColor.GRAY);
                contentPane.add(label, new CC().gapLeft("18").spanX());
            }
        }, new CC().growX().pushX());

        // 恢复选择设置
        topPanel.add(new TitledPanel(message("settingPanel.restoreSelectionSettings.title")) {
            @Override
            protected void addComponentToContentPane(JPanel contentPane) {
                contentPane.add(restoreSelectedCheckBox, new CC().wrap("8"));
                JBLabel label = new JBLabel(message("settingPanel.restoreSelectionSettings.description"));
                label.setForeground(JBColor.GRAY);
                contentPane.add(label, new CC().gapLeft("18").spanX());
            }
        }, new CC().growX().pushX());

        // 当没有选择文字时,右键菜单是否有添加到笔记本菜单项
        topPanel.add(new TitledPanel(message("settingPanel.menuItemSettings.title")) {
            @Override
            protected void addComponentToContentPane(JPanel contentPane) {
                contentPane.add(addMenuItemVisibleCheckBox, new CC().wrap("8"));
                JBLabel label = new JBLabel(message("settingPanel.menuItemSettings.description"));
                label.setForeground(JBColor.GRAY);
                contentPane.add(label, new CC().gapLeft("18").spanX());
            }
        }, new CC().growX().pushX());

        // item太长时,能否超出列表/表格进行显示
        topPanel.add(new TitledPanel(message("settingPanel.titleExpandableSettings.title")) {
            @Override
            protected void addComponentToContentPane(JPanel contentPane) {
                contentPane.add(itemExpandableCheckBox, new CC().wrap("8"));
                JBLabel label = new JBLabel(message("settingPanel.titleExpandableSettings.description"));
                label.setForeground(JBColor.GRAY);
                contentPane.add(label, new CC().gapLeft("18").spanX());
            }
        }, new CC().growX().pushX());

        //表格聚焦时，是否显示边框
        topPanel.add(new TitledPanel(message("settingPanel.showFocusBorderSettings.title")) {
            @Override
            protected void addComponentToContentPane(JPanel contentPane) {
                contentPane.add(showFocusBorderCheckBox, new CC().wrap("8"));
                JBLabel label = new JBLabel(message("settingPanel.showFocusBorderSettings.description"));
                label.setForeground(JBColor.GRAY);
                contentPane.add(label, new CC().gapLeft("18").spanX());
            }
        }, new CC().growX().pushX());

        //缩略图尺寸
        topPanel.add(new TitledPanel(message("settingPanel.thumbnail.title")) {
            @Override
            protected void addComponentToContentPane(JPanel contentPane) {
                contentPane.add(new JLabel(message("settingPanel.thumbnail.setLabel")),"gapright 6");
                contentPane.add(thumbMaxSizeField, new CC().wrap("8"));
                JBLabel label = new JBLabel(message("settingPanel.thumbnail.sizeRange")+": ["+PluginConstant.ThumbSizeMini+" - "+PluginConstant.ThumbSizeMax+"];");
                label.setForeground(JBColor.GRAY);
                contentPane.add(label, new CC().gapLeft("18").spanX());
            }
        }, new CC().growX().pushX());

        // 清理缓存
        //topPanel.add(new TitledPanel(message("settingPanel.cache.title")) {
        //    @Override
        //    protected void addComponentToContentPane(JPanel contentPane) {
        //        JBLabel sizeLabel = new JBLabel("0 Byte");
        //
        //        JButton clearThumb = new JButton(message("settingPanel.cache.button.clear"));
        //
        //        clearThumb.setEnabled(false);
        //        clearThumb.addActionListener(e -> {
        //            boolean busy = isClearing.get();
        //            //如果没有进行清理工作, 那么进行清理
        //            if (!busy) {
        //                isClearing.set(true);
        //                sizeLabel.setText(message("settingPanel.cache.cleaningUp"));
        //                Application application = ApplicationManager.getApplication();
        //                application.executeOnPooledThread(() -> {
        //                    try {
        //                        Thread.sleep(500);
        //                    } catch (InterruptedException interruptedException) {
        //                        interruptedException.printStackTrace();
        //                    }
        //                    File thumbDir = PluginConstant.TEMP_IMAGE_DIRECTORY_PATH.toFile();
        //                    CustomFileUtil.deleteFilesUnderDirectory(thumbDir);
        //                    sizeLabel.setText("0 Byte");
        //                    isClearing.set(false);
        //                });
        //            }
        //        });
        //        contentPane.add(clearThumb, "gapright 8");
        //
        //        //通过一个线程去获取大小
        //        ApplicationManager.getApplication().executeOnPooledThread(() -> {
        //            File thumbDir = PluginConstant.TEMP_IMAGE_DIRECTORY_PATH.toFile();
        //            String dirSize = StringUtil.formatFileSize(CustomFileUtil.sizeOfDirectory(thumbDir));
        //            sizeLabel.setText(dirSize);
        //            clearThumb.setEnabled(true);
        //        });
        //        contentPane.add(sizeLabel,"wrap 8");
        //    }
        //}, new CC().growX().pushX());

        // 模板调整
        topPanel.add(new TitledPanel(message("settingPanel.templateSettings.title")) {
            @Override
            protected void addComponentToContentPane(JPanel contentPane) {

                JBLabel labelRestore = new JBLabel(message("settingPanel.templateSettings.label.text"));
                contentPane.add(labelRestore, new CC().gapRight("6"));
                JButton btnRestoreDefaultTemplate = new JButton(message("settingPanel.templateSettings.button.text"));
                btnRestoreDefaultTemplate.addActionListener(e -> markdownTemplateArea.setText(AppSettingsState.MARKDOWN_GROOVY_TEMPLATE));
                contentPane.add(btnRestoreDefaultTemplate, "wrap 8");

                JBLabel label = new JBLabel(message("settingPanel.templateSettings.description"));
                label.setForeground(JBColor.GRAY);
                contentPane.add(label, new CC().gapLeft("18").spanX().wrap("8"));
            }
        }, new CC().growX().pushX());

        //topPanel.add(new JPanel(), new CC().growY().pushY());
        settingsPanel.add(topPanel, BorderLayout.NORTH);

        JBScrollPane centerPanel = new JBScrollPane(markdownTemplateArea);
        centerPanel.setPreferredSize(new Dimension(500, 400));
        settingsPanel.add(centerPanel);

        //其他> 支持/打赏
        LinkLabel<Object> supportLinkLabel = new LinkLabel<Object>(message("settingPanel.link.support.text"), PluginIcons.Support);
        supportLinkLabel.setListener((aSource, aLinkData) -> {
            new SupportDialog().show();
        }, null);

        LinkLabel<Object> contactLabel = new LinkLabel<Object>(message("settingPanel.link.contact.text"), PluginIcons.Email);
        contactLabel.setListener((aSource, aLinkData) -> {
            String content =
                    "QQ 群号: <a  target='_blank' href='#qq_group'><b>" + PluginConstant.QQ_GROUP + "</b></a>(点击复制群号)<br/>" +
                            "Github:<a href='#github'><b>" + PluginConstant.URL_GITHUB + "</b></a>(点击访问)<br/>" +
                            "163：<a href='#mail-163'><b>" + PluginConstant.EMAIL_163 + "</b></a> (点击发送邮件)<br/>" +
                            "GMail：<a href='#mail-g'><b>" + PluginConstant.EMAIL_GMAIL + "</b></a> (点击发送邮件)<br/>";
            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder(content, null, new JBColor(0xE4E6EB, 0x45494B), new HyperlinkListener() {
                        @Override
                        public void hyperlinkUpdate(HyperlinkEvent e) {
                            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                                String description = e.getDescription();
                                if ("#qq_group".equals(description)) {
                                    PluginConstant.CLIPBOARD.setContents(new StringSelection(PluginConstant.QQ_GROUP), null);
                                } else if ("#github".equals(description)) {
                                    BrowserUtil.browse(PluginConstant.URL_GITHUB);
                                } else if ("#mail-g".equals(description)) {
                                    mailTo(PluginConstant.EMAIL_GMAIL);
                                } else if ("#mail-163".equals(description)) {
                                    mailTo(PluginConstant.EMAIL_163);
                                }
                            }
                        }
                    }).setShadow(true)
                    .setHideOnAction(true)
                    .setHideOnClickOutside(true)
                    .setHideOnFrameResize(true)
                    .setHideOnKeyOutside(true)
                    .setHideOnLinkClick(true)
                    .setContentInsets(JBUI.insets(10))
                    .createBalloon()
                    .show(JBPopupFactory.getInstance().guessBestPopupLocation(contactLabel), Balloon.Position.above);
        }, null);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        southPanel.add(supportLinkLabel);
        southPanel.add(contactLabel);
        settingsPanel.add(southPanel, BorderLayout.SOUTH);
    }

    public JPanel getSettingsPanel() {
        return settingsPanel;
    }

    public String getCustomFontName() {
        return fontComboBox.getFontName();
    }

    public void setCustomFontName(String fontName) {
        fontComboBox.setFontName(fontName);
    }

    public int getCustomFontSize() {
        int size = CustomUIUtil.getMyDefaultFont().getSize();
        Object obj = fontSizeBox.getSelectedItem();

        //如果为空, 那么返回默认字体大小
        if (obj == null) {
            return size;
        }

        try {
            final int i = (Integer) obj;
            //如果小于最小字体 ,那么设置为最小的字体
            if (i < PluginConstant.MIN_FONT_SIZE) {
                size = PluginConstant.MIN_FONT_SIZE;
            } else {
                size = Math.min(i, PluginConstant.MAX_FONT_SIZE);
            }
            return size;
        } catch (Exception e) {
            e.printStackTrace();
            return size;

        }
    }

    public void setThumbMaxSize(int size) {
        thumbMaxSizeField.setValue(size);
    }

    public int getThumbMaxSize() {
        return thumbMaxSizeField.getValue();
    }

    public void setCustomFontSize(int size) {
        if (size < PluginConstant.MIN_FONT_SIZE) {
            size = PluginConstant.MIN_FONT_SIZE;
        } else if (size > PluginConstant.MAX_FONT_SIZE) {
            size = PluginConstant.MAX_FONT_SIZE;
        }
        fontSizeBox.setSelectedItem(size);
    }

    public boolean getRestoreSelected() {
        return restoreSelectedCheckBox.isSelected();
    }

    public void setRestoreSelected(boolean isRestore) {
        restoreSelectedCheckBox.setSelected(isRestore);
    }

    public boolean getAddMenuItemVisibleStatus() {
        return addMenuItemVisibleCheckBox.isSelected();
    }

    public void setAddMenuItemVisibleStatus(boolean visible) {
        addMenuItemVisibleCheckBox.setSelected(visible);
    }

    public boolean getItemExpandableStatus() {
        return itemExpandableCheckBox.isSelected();
    }

    public void setItemExpandableStatus(boolean expandable) {
        itemExpandableCheckBox.setSelected(expandable);
    }

    public String getMarkdownTemplate() {
        return markdownTemplateArea.getText();
    }

    public void setMarkdownTemplate(String template) {
        markdownTemplateArea.setText(template);
    }

    public void setShowFocusBorder(boolean visible) {
        showFocusBorderCheckBox.setSelected(visible);
    }

    public boolean getShowFocusBorder() {
        return showFocusBorderCheckBox.isSelected();
    }

    private void mailTo(String emailPath) {
        try {
            URI uri = new URIBuilder()
                    .setScheme("mailto")
                    .setPath(emailPath)
                    .setParameter("subject", "Plugin: Notebook")
                    .build();
            Desktop.getDesktop().mail(uri);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }



}
