package com.itcodebox.notebooks.ui.tables;

import com.intellij.util.ui.JBFont;
import icons.PluginIcons;

import javax.swing.*;
import java.awt.*;

/**
 * @author LeeWyatt
 */
public class ChapterTableCellRenderer extends AbstractRecordTableCellRenderer {

    @Override
    public Font getCellFont() {
        return JBFont.label().biggerOn(2);
    }

    @Override
    public FlowLayout getCellLayout() {
        return new FlowLayout(FlowLayout.LEFT, 10, 10);
    }

    @Override
    public Icon getDefaultIcon() {
        return PluginIcons.ChapterCell;
    }

    @Override
    public Icon getSelectedIcon() {
        return PluginIcons.CellSelected;
    }

    @Override
    public Icon getDropOnIcon() {
        return PluginIcons.DragToAdd;
    }
}
