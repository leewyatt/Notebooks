package com.itcodebox.notebooks.ui.dialog;

import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.HighlightableComponent;
import com.intellij.ui.JBColor;
import com.itcodebox.notebooks.constant.PluginColors;
import com.itcodebox.notebooks.entity.SearchRecord;
import com.itcodebox.notebooks.utils.StringUtil;
import icons.PluginIcons;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author LeeWyatt
 */
public class SearchRecordCellRender implements ListCellRenderer<SearchRecord> {
    private static  final TextAttributes ATTRIBUTES = new TextAttributes( JBColor.ORANGE,null, null,null, Font.BOLD);

    @Override
    public Component getListCellRendererComponent(JList<? extends SearchRecord> listView, SearchRecord record, int index, boolean isSelected, boolean cellHasFocus) {
        FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);

        JPanel panel = new JPanel(layout);
        HighlightableComponent component = new HighlightableComponent();
        if (record != null) {

            JLabel iconLabel = new JLabel();
            if (record.getNoteId() != null) {
                iconLabel.setIcon(PluginIcons.NoteCell);
            } else if (record.getChapterId() != null) {
                iconLabel.setIcon(PluginIcons.ChapterCell);
            } else if (record.getNotebookId() != null) {
                iconLabel.setIcon(PluginIcons.NotebookCell);
            }
            panel.add(iconLabel);
            String s = record.getPath("   ");
            boolean flag = false;
            String path = record.getPath(" > ");
            component.setText(path);
            component.setOpaque(false);
            if (keywords != null) {
                for (String keyword : keywords) {
                    List<Integer> stringIndexList = StringUtil.getStringIndex(s, keyword);
                    for (Integer integer : stringIndexList) {
                        flag =true;
                        component.addHighlighter(integer,integer+keyword.length(),ATTRIBUTES);
                    }
                }
                if (!flag) {
                    component.setText(path +" [ ... ]");
                    component.addHighlighter(path.length()+3,path.length()+6,ATTRIBUTES);
                }
            }
        }
        Color background;
        if (isSelected) {
            background = PluginColors.NOTE_GROUP_SELECTED;
            // unselected, and not the DnD drop location
        } else {
            // 3.设置单数行，偶数行的颜色
            if (index % 2 == 0) {
                // 偶数行时的颜色
                background = PluginColors.EVEN_ROW;
            } else {
                // 设置单数行的颜色
                background = PluginColors.ODD_ROW;
            }
        }
        panel.add(component);
        panel.setBackground(background);
        //component.setBackground(background);
        component.setForeground(isSelected?JBColor.WHITE:JBColor.BLACK);
        return panel;
    }

    private String[] keywords;
    public void setKeywords(String[] keywords){
        this.keywords = keywords;
    }


}
