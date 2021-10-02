package com.itcodebox.notebooks.utils;

import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.ui.JBSplitter;
import com.itcodebox.notebooks.ui.panes.DetailPanel;
import com.itcodebox.notebooks.ui.panes.MainPanel;

import java.lang.reflect.Method;

/**
 * @Author LittleRed
 * @Date 2021/10/2 18:48
 * @Description TODO
 * @Since version-1.0
 */
public class FocusUtil {

    /**
     * 使打开Notebook时焦点在编辑区
     * @param mainPanel 
     */
    public static void getEditorFocus(MainPanel mainPanel) {
        // 获取Editor对象
        JBSplitter contentPane = (JBSplitter) mainPanel.getComponent(0);
        JBSplitter rightPane = (JBSplitter) contentPane.getSecondComponent();
        DetailPanel detailPanel = (DetailPanel) rightPane.getSecondComponent();
        EditorImpl fieldContent = (EditorImpl) detailPanel.getFieldContent();
        // 调用requestFouces()方法获取焦点
        try {
            Method requestFocus = fieldContent.getClass().getDeclaredMethod("requestFocus");
            requestFocus.setAccessible(true);
            requestFocus.invoke(fieldContent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
