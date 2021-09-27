package com.itcodebox.notebooks.ui.tables;

import com.intellij.util.ui.JBFont;
import icons.PluginIcons;

import javax.swing.*;
import java.awt.*;

/**
 * @author LeeWyatt
 */
public class NoteTableCellRenderer extends AbstractRecordTableCellRenderer {

    @Override
    public Font getCellFont() {
        return JBFont.label().biggerOn(2);
    }

    @Override
    public FlowLayout getCellLayout() {
        return new FlowLayout(FlowLayout.LEFT, 10, 5);
    }

    @Override
    public Icon getDefaultIcon() {
        return PluginIcons.NoteCell;
    }

    @Override
    public Icon getSelectedIcon() {
        return PluginIcons.CellSelected;
    }
}
