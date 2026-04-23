package com.itcodebox.notebooks.projectservice;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * storages—一个或多个@com.intellij.openapi.components.Storage注释，用于指定存储位置。对于项目级别的值是可选的-在这种情况下，将使用标准项目文件。
 * 所以这里和AppSettingsState比省略了storages注解
 *
 * @author LeeWyatt
 */
@State(
        name = "com.itcodebox.notebooks.projectservice.ProjectUIState"
)
public class ProjectStorage implements PersistentStateComponent<ProjectStorage> {

    public boolean notebookPaneVisible = false;
    public boolean chapterPaneVisible = false;
    public boolean notePaneVisible = false;

    public int selectedNotebookId = -1;
    public int selectedChapterId = -1;
    public int selectedNoteId = -1;

    /**
     * Splitter proportions, persisted per-project so that resized column widths
     * survive an IDE restart (fixes GitHub #8: "default width"). Defaults picked
     * per GitHub #7 ("界面四块区域的布局比例"): the original 0.5/0.5/0.5 equal
     * split left too little room for note content — these ratios give roughly
     *
     *     notebook : chapter : note : detail  =  15% : 15% : 14% : 56%
     *
     * of the tool-window width when all four columns are visible.
     *
     * MainPanel writes back to these fields on every proportion change, so
     * manual resizes are saved automatically without any explicit "save now"
     * action.
     */
    public float contentPaneProportion = 0.3f;  // (notebook+chapter) : (note+detail)
    public float leftPaneProportion = 0.5f;     // notebook : chapter
    public float rightPaneProportion = 0.2f;    // note : detail


    @Override
    public @Nullable
    ProjectStorage getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ProjectStorage projectStorage) {
        XmlSerializerUtil.copyBean(projectStorage, this);
    }
}
