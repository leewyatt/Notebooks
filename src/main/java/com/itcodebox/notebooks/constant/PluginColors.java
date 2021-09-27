package com.itcodebox.notebooks.constant;

import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import java.awt.*;

/**
 * @author LeeWyatt
 */
public interface PluginColors {
    JBColor ODD_ROW = new JBColor(Gray._255, Gray._63);
    JBColor EVEN_ROW = new JBColor(Gray._245, Gray._69);
    JBColor TEXT_TITLE = new JBColor(Gray._128, Gray._188);
    JBColor TEXT_TITLE_SELECTED = new JBColor(Gray._240, Gray._250);
    JBColor NOTE_GROUP_SELECTED = new JBColor(new Color(38, 117, 191), new Color(75, 110, 175));
    JBColor DROP_ON = new JBColor(new Color(138, 188, 241), new Color(119, 151, 212));
    JBColor WARN_COLOR = new JBColor(new Color(219,88,96),new Color(199,84,80));
    Color INSERT_BALLOON_BORDER = new Color(64, 64, 64, 90);

    JBColor NORMAL_BORDER_COLOR = new JBColor(Gray._242, new Color(60, 63, 65));
    JBColor WARING_BORDER_COLOR = new JBColor(new Color(219, 88, 96), new Color(199, 84, 80));

}
