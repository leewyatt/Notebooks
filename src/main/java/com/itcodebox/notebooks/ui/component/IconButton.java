package com.itcodebox.notebooks.ui.component;

import com.intellij.openapi.actionSystem.ActionButtonComponent;
import com.intellij.openapi.actionSystem.ex.ActionButtonLook;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBEmptyBorder;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author LeeWyatt
 */
public class IconButton extends JBLabel {

    private boolean hovered = false;
    private final IconButton lb = this;

    private static final JBEmptyBorder OUTSIDE_BORDER = new JBEmptyBorder(3, 3, 3, 3);
    protected static final Border  RAISE_BORDER = BorderFactory.createCompoundBorder(OUTSIDE_BORDER, BorderFactory.createRaisedBevelBorder());
    protected static final Border BEVEL_BORDER = BorderFactory.createCompoundBorder(OUTSIDE_BORDER, BorderFactory.createLoweredBevelBorder());

    public IconButton(@Nullable String text, @NotNull Icon icon, @NotNull Function1<? super JComponent, Unit> onClickHandler) {
        super(icon);
        if (text != null) {
            setToolTipText(text);
        }
        setBorder(RAISE_BORDER);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    onClickHandler.invoke(lb);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                lb.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                lb.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                lb.requestFocus();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    lb.setBorder(BEVEL_BORDER);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lb.requestFocus();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    lb.setBorder(RAISE_BORDER);
                }
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        if (hovered) {
            ActionButtonLook.SYSTEM_LOOK.paintBackground(g, lb, ActionButtonComponent.SELECTED);
        }
        super.paintComponent(g);
    }
}
