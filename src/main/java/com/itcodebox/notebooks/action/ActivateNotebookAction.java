package com.itcodebox.notebooks.action;

import com.intellij.ide.actions.ActivateToolWindowAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBSplitter;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.projectservice.NotebooksUIManager;
import com.itcodebox.notebooks.ui.panes.DetailPanel;
import com.itcodebox.notebooks.ui.panes.MainPanel;

import java.lang.reflect.Method;

public class ActivateNotebookAction extends DumbAwareAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        
        Project eventProject = ActivateToolWindowAction.getEventProject(e);
        ToolWindow toolWindow = ToolWindowManager.getInstance(eventProject).getToolWindow(PluginConstant.TOOLWINDOW_ID);
        toolWindow.show();

        NotebooksUIManager uiManager = ServiceManager.getService(eventProject, NotebooksUIManager.class);
        MainPanel mainPanel = uiManager.getMainPanel();
        // 获取Editor对象
        JBSplitter contentPane = (JBSplitter) mainPanel.getComponent(0);
        JBSplitter rightPane = (JBSplitter) contentPane.getSecondComponent();
        DetailPanel detailPanel = (DetailPanel) rightPane.getSecondComponent();
        EditorImpl fieldContent = (EditorImpl) detailPanel.getFieldContent();
        // 打开Notebook时焦点在编辑区
        try {
            Method requestFocus = fieldContent.getClass().getDeclaredMethod("requestFocus");
            requestFocus.setAccessible(true);
            requestFocus.invoke(fieldContent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
