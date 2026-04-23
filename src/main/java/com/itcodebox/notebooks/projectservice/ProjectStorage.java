package com.itcodebox.notebooks.projectservice;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.itcodebox.notebooks.entity.LayoutMode;
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

    /**
     * Legacy fields (plugin &lt; 1.42). Kept so XML saved by older versions
     * still deserializes; {@link #loadState} migrates them into
     * {@link #layoutMode} on first load after upgrade. New writes only touch
     * {@code layoutMode}. Defaults match the original (all-false = legacy
     * "content only" first-run layout).
     */
    public boolean notebookPaneVisible = false;
    public boolean chapterPaneVisible = false;
    public boolean notePaneVisible = false;

    /**
     * Which top-level panels are visible in the tool window. Source of truth
     * from 1.42 onwards. Persisted as enum name via XmlSerializer. Defaults
     * to {@link LayoutMode#FULL} — new installs see the full four-column view
     * so they can discover the notebook/chapter/note hierarchy without
     * needing to hunt for a "show everything" toggle.
     */
    public LayoutMode layoutMode = LayoutMode.FULL;

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
        // Keep legacy booleans in sync with layoutMode so a downgrade to a
        // plugin version < 1.42 can still read meaningful visibility state
        // from the XML (layoutMode will be ignored by older code).
        notebookPaneVisible = layoutMode.isNotebookVisible();
        chapterPaneVisible = layoutMode.isChapterVisible();
        notePaneVisible = layoutMode.isNoteVisible();
        return this;
    }

    @Override
    public void loadState(@NotNull ProjectStorage projectStorage) {
        XmlSerializerUtil.copyBean(projectStorage, this);
        // Upgrading from < 1.42: the XML has per-panel visibility booleans but
        // no layoutMode. If XmlSerializer left layoutMode at its Java default
        // (FULL) while any of the legacy booleans are non-default, assume the
        // user had customized visibility and recover that mode. After this
        // one-time migration the booleans are ignored on read (getState still
        // serializes them, but they're orthogonal to layoutMode going forward).
        boolean anyLegacyVisible = notebookPaneVisible || chapterPaneVisible || notePaneVisible;
        if (this.layoutMode == LayoutMode.FULL && anyLegacyVisible
                && !(notebookPaneVisible && chapterPaneVisible && notePaneVisible)) {
            this.layoutMode = LayoutMode.fromLegacyVisibility(
                    notebookPaneVisible, chapterPaneVisible, notePaneVisible);
        }
    }
}
