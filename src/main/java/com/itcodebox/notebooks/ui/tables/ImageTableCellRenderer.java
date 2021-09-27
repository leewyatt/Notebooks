package com.itcodebox.notebooks.ui.tables;

import com.intellij.util.ui.JBEmptyBorder;
import com.intellij.util.ui.JBUI;
import com.itcodebox.notebooks.constant.PluginColors;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.entity.ImageRecord;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author LeeWyatt
 */
public class ImageTableCellRenderer implements TableCellRenderer {

    private @NotNull Icon getIcon(String path) {
        if (path == null || path.trim().isEmpty()) {
            return PluginIcons.Unknown;
        }
        String fileType = path.toLowerCase();
        Icon icon;
        if (fileType.endsWith(PluginConstant.GIF)) {
            icon = PluginIcons.GIF;
        } else if (fileType.endsWith(PluginConstant.JPG) || fileType.endsWith(PluginConstant.JPEG)) {
            icon = PluginIcons.JPG;
        } else if (fileType.endsWith(PluginConstant.PNG)) {
            icon = PluginIcons.PNG;
        } else {
            icon = PluginIcons.Unknown;
        }
        return icon;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        ImageRecord imageRecord = (ImageRecord) value;
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new JBEmptyBorder(6,3,6,3));
        JLabel imageLabel = new JLabel(getIcon(imageRecord.getImagePath()));
        JLabel textLabel = new JLabel(imageRecord.getImageTitle());
        textLabel.setBorder(new JBEmptyBorder(0,5,0,0));
        textLabel.setFont(JBUI.Fonts.label().asBold());
        panel.add(imageLabel, BorderLayout.WEST);
        panel.add(textLabel);
        Color background;
        Color foreground;
        // check if this cell represents the current DnD drop location
        JTable.DropLocation dropLocation = table.getDropLocation();
        if (isSelected) {
            background = PluginColors.NOTE_GROUP_SELECTED;
            foreground = PluginColors.TEXT_TITLE_SELECTED;
        } else {
            // 3.设置单数行，偶数行的颜色
            if (row % 2 == 0) {
                // 偶数行时的颜色
                background = PluginColors.EVEN_ROW;
            } else {
                // 设置单数行的颜色
                background = PluginColors.ODD_ROW;
            }
            foreground = PluginColors.TEXT_TITLE;
        }
        panel.setBackground(background);
        textLabel.setForeground(foreground);
        return panel;
    }
}
