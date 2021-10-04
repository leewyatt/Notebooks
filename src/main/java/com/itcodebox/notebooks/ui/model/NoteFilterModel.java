package com.itcodebox.notebooks.ui.model;

import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.itcodebox.notebooks.entity.NoteNavigationItem;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @Author LittleRed
 * @Date 2021/10/2 15:00
 * @Description TODO
 * @Since version-1.0
 */
public class NoteFilterModel extends FilteringGotoByModel<NoteNavigationItem.NameAndValue> {
    
    public NoteFilterModel(@NotNull Project project, @NotNull ChooseByNameContributor[] contributors) {
        super(project, contributors);
    }

    /**
     * @description: 命中选项
     * @return java.lang.Object
     */
    @Nullable
    @Override
    protected NoteNavigationItem.NameAndValue filterValueFor(NavigationItem navigationItem) {
        if (navigationItem instanceof NoteNavigationItem) {
            NoteNavigationItem myNavigationItem = (NoteNavigationItem) navigationItem;
            return myNavigationItem.getNameAndValue();
        }
        return null;
    }

    /**
     * @description: 搜索框标题
     * @return java.lang.String
     */
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @Override
    public String getPromptText() {
        return "SearchNote";
    }

    @NotNull
    @Override
    public String getNotInMessage() {
        return "Not in Message";
    }

    @NotNull
    @Override
    public String getNotFoundMessage() {
        return "Not Found message";
    }

    /**
     * @description: 过滤器是否打开
     * @return java.lang.String
     */
    @Nullable
    @Override
    public String getCheckBoxName() {
        return null;
    }

    @Override
    public boolean loadInitialCheckBoxState() {
        return false;
    }

    @Override
    public void saveInitialCheckBoxState(boolean b) {
    }

    @NotNull
    @Override
    public String[] getSeparators() {
        return new String[]{"/","?"};
    }

    /**
     * @description: 必须重写，返回数据项
     * @return java.lang.String
     */
    @Nullable
    @Override
    public String getFullName(@NotNull Object element) {
        return ((NoteNavigationItem)element).getValue();
    }

    @Override
    public boolean willOpenEditor() {
        return true;
    }

}
