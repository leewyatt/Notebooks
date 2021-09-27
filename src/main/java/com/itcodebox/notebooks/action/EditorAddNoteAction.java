package com.itcodebox.notebooks.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.ui.dialog.AddNoteDialog;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsState;
import org.jetbrains.annotations.NotNull;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class EditorAddNoteAction extends DumbAwareAction {

    /**
     * 根据不同的情况,来判断是否禁用以及是否显示添加笔记的菜单项
     * 如果没有选择任何文字, 那么不会显示添加笔记的菜单项
     */
    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        //设置可见性并仅在现有项目和编辑器以及存在选择的情况下启用
        e.getPresentation().setEnabledAndVisible(
                //!AppSettingsState.getInstance().readOnlyMode&&
                project != null
                        && editor != null
                        && psiFile != null
                        && (AppSettingsState.getInstance().addMenuItemVisible || editor.getSelectionModel().hasSelection())
        );
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        // e.getDate 和 e.getRequiredData 的区别,
        // 1. e.getDate 可能返回null
        //    e.getRequiredData 不会返回null ,如果为null ,那么会抛异常
        // 2. 为了避免 e.getRequiredData返回null导致错误,所以在 update里进行了检查
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        if (AppSettingsState.getInstance().readOnlyMode) {
            Messages.showInfoMessage(project, message("popupAction.busy.message"), message("popupAction.busy.title"));
            return;
        }
        PsiFile psiFile = e.getRequiredData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        // 获得选择开始的光标偏移量(当没有选择时, 主光标的位置一致)
        SelectionModel selectionModel = editor.getSelectionModel();
        int offsetStart = selectionModel.getSelectionStart();
        int offsetEnd = selectionModel.getSelectionEnd();
        String content = selectionModel.getSelectedText();

        // 虚拟文件
        VirtualFile virtualFile = psiFile.getVirtualFile();
        // 获取文件类型,而非后缀
        String language = psiFile.getLanguage().getDisplayName().toLowerCase();
        // 获取文件的路径
        String filePath = virtualFile.getPath();
        //设置属性
        Note note = new Note();
        note.setContent(content);
        note.setType(language);
        note.setSource(filePath);
        note.setOffsetStart(offsetStart);
        note.setOffsetEnd(offsetEnd);
        //显示添加对话框
        new AddNoteDialog(project, note).show();
    }
}
