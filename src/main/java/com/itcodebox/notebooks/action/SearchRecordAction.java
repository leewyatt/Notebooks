package com.itcodebox.notebooks.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.itcodebox.notebooks.ui.dialog.SearchDialog;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class SearchRecordAction extends DumbAwareAction {


    public SearchRecordAction() {
        super(message("detailPanel.action.search.text"), "", PluginIcons.Search);
    }

    /**
     * Unlike the sibling editor actions, this one has no menu-context guarantee:
     * it also carries a global {@code alt s} shortcut and is reused in the note
     * toolbar, so PROJECT is not guaranteed present. Guard it here (disable +
     * hide when absent) instead of assuming non-null in {@link #actionPerformed}.
     */
    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(e.getData(CommonDataKeys.PROJECT) != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = Objects.requireNonNull(anActionEvent.getData(CommonDataKeys.PROJECT), "PROJECT is missing");
        new SearchDialog(project).show();
    }
    
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread ()
    {
        return ActionUpdateThread.BGT;
    }
}
