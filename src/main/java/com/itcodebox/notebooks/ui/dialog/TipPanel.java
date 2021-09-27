package com.itcodebox.notebooks.ui.dialog;

import com.intellij.ui.components.JBScrollPane;
import com.itcodebox.notebooks.utils.CustomUIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author LeeWyatt
 */
public class TipPanel extends JPanel {

    public TipPanel(@NotNull Tip tip) {
        setLayout(new BorderLayout());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><body style='background-color:white;color:black'>");
        stringBuilder.append("<h2 style='font-size:18'>").append(tip.getTitle()).append("</h2><ul style='font-size:16'>");
        String[] infos = tip.getInfos();
        for (String info : infos) {
            stringBuilder.append("<li>").append(info).append("</li>");
        }
        stringBuilder.append("</ul>");
        String imgPath = tip.getImgPath();
        if (imgPath != null && !imgPath.trim().isEmpty()) {
            stringBuilder.append("<img src='").append(CustomUIUtil.getImgScr(imgPath)).append("'/>" );
        }
        stringBuilder.append("</body></html>");
        JEditorPane editorPane = new JEditorPane("text/html", stringBuilder.toString());
        editorPane.setEditable(false);
        JBScrollPane scrollPane = new JBScrollPane(editorPane);
        add(scrollPane, BorderLayout.CENTER);
    }
}