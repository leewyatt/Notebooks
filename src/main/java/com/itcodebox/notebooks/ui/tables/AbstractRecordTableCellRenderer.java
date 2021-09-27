package com.itcodebox.notebooks.ui.tables;

import com.intellij.ui.components.JBLabel;
import com.itcodebox.notebooks.constant.PluginColors;
import com.itcodebox.notebooks.entity.Record;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author LeeWyatt
 */
public abstract class AbstractRecordTableCellRenderer implements TableCellRenderer {

    /**
     * 用于给TableCell设置字体
     *
     * @return 字体
     */
    public abstract Font getCellFont();

    /**
     * 用于设置布局
     *
     * @return 布局
     */
    public abstract FlowLayout getCellLayout();

    /**
     * 用于设置默认的图标
     *
     * @return 图标
     */
    public abstract Icon getDefaultIcon();

    /**
     * 用于设置选择状态下的图标
     *
     * @return 图标
     */
    public abstract Icon getSelectedIcon();

    public Icon getDropOnIcon() {
        return null;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        //JBLabel参数要求非空,需要一定要判断仔细
        String result;
        if (value == null) {
            result = "";
        } else {
            String temp = ((Record) value).getTitle();
            result = temp == null ? "" : temp;
        }

        JPanel panel = new JPanel(getCellLayout());
        JBLabel label = new JBLabel(result);
        Font cellFont = getCellFont();
        label.setFont(cellFont);
        panel.add(label);
        Color background;
        Color foreground;
        // check if this cell represents the current DnD drop location
        JTable.DropLocation dropLocation = table.getDropLocation();
        if (isSelected) {
            label.setIcon(getSelectedIcon());
            background = PluginColors.NOTE_GROUP_SELECTED;
            foreground = PluginColors.TEXT_TITLE_SELECTED;
            // unselected, and not the DnD drop location
        } else if (dropLocation != null && !dropLocation.isInsertRow()
                && dropLocation.getRow() == row) {
            if (getDropOnIcon() != null) {
                label.setIcon(getDropOnIcon());
            }
            background = PluginColors.DROP_ON;
            foreground = PluginColors.TEXT_TITLE_SELECTED;
            // check if this cell is selected
        } else {
            label.setIcon(getDefaultIcon());
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
        label.setForeground(foreground);
        return panel;
    }

}
