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
        toolWindow.show();

        NotebooksUIManager uiManager = ServiceManager.getService(eventProject, NotebooksUIManager.class);
        MainPanel mainPanel = uiManager.getMainPanel();
        FocusUtil.getEditorFocus(mainPanel);

    }
}
