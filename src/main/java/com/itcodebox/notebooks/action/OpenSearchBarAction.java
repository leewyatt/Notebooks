package com.itcodebox.notebooks.action;

import com.intellij.ide.actions.ActivateToolWindowAction;
import com.intellij.ide.actions.GotoActionBase;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.itcodebox.notebooks.entity.NoteNavigationItem;
import com.itcodebox.notebooks.entity.SearchRecord;
import com.itcodebox.notebooks.projectservice.NotebooksUIManager;
import com.itcodebox.notebooks.service.impl.NoteChooseByname;
import com.itcodebox.notebooks.ui.model.NoteFilterModel;
import com.itcodebox.notebooks.ui.panes.MainPanel;
import com.itcodebox.notebooks.ui.tables.ChapterTable;
import com.itcodebox.notebooks.ui.tables.NoteTable;
import com.itcodebox.notebooks.ui.tables.NotebookTable;
import com.itcodebox.notebooks.utils.FocusUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@SuppressWarnings("unchecked")
public class OpenSearchBarAction extends GotoActionBase {

    @Override
    protected void gotoActionPerformed(@NotNull AnActionEvent e) {
        NoteChooseByname chooseByname = new NoteChooseByname();
        ChooseByNameContributor[] butor = new ChooseByNameContributor[]{chooseByname};
        NoteFilterModel model = new NoteFilterModel(e.getProject(), butor);

        // 初始化回调函数
        GotoActionCallback callback = new GotoActionCallback() {

            /**
             * 选择选项后的响应
             * @param chooseByNamePopup
             * @param o
             */
            @Override
            public void elementChosen(ChooseByNamePopup chooseByNamePopup, Object o) {
                Project eventProject = ActivateToolWindowAction.getEventProject(e);
                ToolWindow toolWindow = ToolWindowManager.getInstance(e.getProject()).getToolWindow("Notebook");
                toolWindow.show();

                // 获取被选中的对象
                NoteNavigationItem chosenElement = (NoteNavigationItem) chooseByNamePopup.getChosenElement();
                SearchRecord searchRecord = NoteChooseByname.records.parallelStream()
                        .filter(p -> p.toString().equals(chosenElement.getValue())).findFirst().get();

                NotebooksUIManager uiManager = e.getProject().getService(NotebooksUIManager.class);
                MainPanel mainPanel = uiManager.getMainPanel();
                // 通过搜索框打开Notebook自动收缩笔记选择栏
                JComponent northPanel = mainPanel.getDetailPanel().getNorthPanel();
                northPanel.setVisible(false);
                // 此处需要更换相应的Icon
                // presentation.setIcon(AllIcons.Actions.Expandall);
                
                // 通过搜索框打开Notebook自动收缩笔记描述栏
                JPanel descScrollPane = mainPanel.getDetailPanel().getDescScrollPane();
                descScrollPane.setVisible(false);
                // presentation.setIcon(PluginIcons.Show);
                // presentation.setText(message("detailPanel.action.showDesc.show"));

                NotebookTable notebookTable = mainPanel.getNotebookTable();
                if (searchRecord.getNotebookId() != null) {
                    notebookTable.selectedRowById(searchRecord.getNotebookId());
                } else {
                    notebookTable.clearSelection();
                }

                ChapterTable chapterTable = mainPanel.getChapterTable();
                if (searchRecord.getChapterId() != null) {
                    chapterTable.selectedRowById(searchRecord.getChapterId());
                } else {
                    chapterTable.clearSelection();
                }

                NoteTable noteTable = mainPanel.getNoteTable();
                if (searchRecord.getNoteId() != null) {
                    noteTable.selectedRowById(searchRecord.getNoteId());
                } else {
                    noteTable.clearSelection();
                }

                FocusUtil.getEditorFocus(mainPanel);
            }
        };

        showNavigationPopup(e, model, callback);
    }

}
