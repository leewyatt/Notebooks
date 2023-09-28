package com.itcodebox.notebooks.action;

import com.intellij.ide.actions.ActivateToolWindowAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.projectservice.NotebooksUIManager;
import com.itcodebox.notebooks.ui.panes.MainPanel;
import com.itcodebox.notebooks.utils.FocusUtil;

public class ActivateNotebookAction extends DumbAwareAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        
        Project eventProject = ActivateToolWindowAction.getEventProject(e);
        ToolWindow toolWindow = ToolWindowManager.getInstance(eventProject).getToolWindow(PluginConstant.TOOLWINDOW_ID);

        NotebooksUIManager uiManager = e.getProject().getService(NotebooksUIManager.class);
        MainPanel mainPanel = uiManager.getMainPanel();
        
        // 如果Notebook已经处于打开状态，则不自动展开①笔记选择栏和②笔记描述栏
        // 如果Notebook处于关闭未打开状态，则通过该方式打开Notebook会自动展开①和②
        if (!toolWindow.isVisible()) {
            toolWindow.show();
            mainPanel = uiManager.getMainPanel();
            mainPanel.getDetailPanel().getNorthPanel().setVisible(true);
            mainPanel.getDetailPanel().getDescScrollPane().setVisible(true);
            // 此处需要更换相应的Icon
        }
        
        // 打开Notebook时编辑区获取焦点
        FocusUtil.getEditorFocus(mainPanel);

    }
}
