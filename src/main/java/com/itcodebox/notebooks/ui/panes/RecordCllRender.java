package com.itcodebox.notebooks.ui.panes;

import com.itcodebox.notebooks.constant.PluginColors;
import com.itcodebox.notebooks.entity.Record;

import javax.swing.*;
import java.awt.*;

/**
 * @author LeeWyatt
 */
public class RecordCllRender implements ListCellRenderer<Record> {
    @Override
    public Component getListCellRendererComponent(JList<? extends Record> list, Record value, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(value==null?"":value.getTitle());
        panel.add(label);
        Color background;
        //Color foreground;
        if (isSelected) {
            background = PluginColors.NOTE_GROUP_SELECTED;
            //foreground = PluginColors.TEXT_TITLE_SELECTED;
        }  else {
            // 3.设置单数行，偶数行的颜色
            if (index % 2 == 0) {
                // 偶数行时的颜色
                background = PluginColors.EVEN_ROW;
            } else {
                // 设置单数行的颜色
                background = PluginColors.ODD_ROW;
            }
            //foreground = PluginColors.TEXT_TITLE;
        }
        panel.setBackground(background);
        //label.setForeground(foreground);
        return panel;
    }
}
