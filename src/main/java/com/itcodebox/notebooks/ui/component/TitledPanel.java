package com.itcodebox.notebooks.ui.component;

import com.intellij.ui.IdeBorderFactory;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

/**
 * @author LeeWyatt
 */
public abstract class TitledPanel extends JPanel {
    public TitledPanel(String title) {
        setLayout(new MigLayout(new LC().fill().gridGap("0!", "0!").insets("0")));
        setBorder(IdeBorderFactory.createTitledBorder(title));
        JPanel contentPane = new JPanel(new MigLayout(new LC().fill().gridGap("0!", "0!").insets("0")));
        addComponentToContentPane(contentPane);
        add(contentPane);
        add(new JPanel(),new CC().growX().pushX());
    }

    /**
     *  添加组件
     * @param contentPane 把组件添加到内容面板
     */
    protected abstract void addComponentToContentPane(JPanel contentPane);

}
