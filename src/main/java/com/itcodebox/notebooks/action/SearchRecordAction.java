package com.itcodebox.notebooks.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.itcodebox.notebooks.ui.dialog.SearchDialog;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class SearchRecordAction extends DumbAwareAction {


    public SearchRecordAction() {
        super(message("detailPanel.action.search.text"), "", PluginIcons.Search);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
        new SearchDialog(project).show();
    }
}
