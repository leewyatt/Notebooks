package com.itcodebox.notebooks.entity;

import icons.PluginIcons;

import javax.swing.Icon;

/**
 * Which top-level panels of the Notebook tool window are visible.
 *
 * <p>Previously the UI offered four {@code DumbAwareToggleAction}s, one per
 * panel. Each had its own {@code isSelected()} based on its own panel's
 * visibility, so in FULL mode three of them appeared "selected" at the same
 * time — giving the misleading impression that they were independent toggles
 * when they were really radio-button-style layout modes. This enum makes the
 * radio semantics explicit and backs the ComboBox that replaced the icons.
 *
 * <p>Each mode also advertises which panels are visible so
 * {@code MainPanel.resetPanesVisible} can apply them without a switch-case.
 */
public enum LayoutMode {
    /** Full 4-column view: notebook + chapter + note + detail. */
    FULL(true, true, true, PluginIcons.NotebookCell, "mainPanel.layout.full"),
    /** 3-column view: chapter + note + detail (notebook list hidden). */
    CHAPTER_PLUS(false, true, true, PluginIcons.ChapterCell, "mainPanel.layout.chapterPlus"),
    /** 2-column view: note list + detail. */
    NOTE_PLUS(false, false, true, PluginIcons.NoteCell, "mainPanel.layout.notePlus"),
    /** 1-column view: only the detail/content pane. */
    CONTENT_ONLY(false, false, false, PluginIcons.Detail, "mainPanel.layout.contentOnly");

    private final boolean notebookVisible;
    private final boolean chapterVisible;
    private final boolean noteVisible;
    private final Icon icon;
    private final String bundleKey;

    LayoutMode(boolean notebookVisible, boolean chapterVisible, boolean noteVisible,
               Icon icon, String bundleKey) {
        this.notebookVisible = notebookVisible;
        this.chapterVisible = chapterVisible;
        this.noteVisible = noteVisible;
        this.icon = icon;
        this.bundleKey = bundleKey;
    }

    public boolean isNotebookVisible() {
        return notebookVisible;
    }

    public boolean isChapterVisible() {
        return chapterVisible;
    }

    public boolean isNoteVisible() {
        return noteVisible;
    }

    public Icon getIcon() {
        return icon;
    }

    public String getBundleKey() {
        return bundleKey;
    }

    /**
     * Recover the mode implied by three legacy per-panel visibility booleans —
     * used once at load time to migrate {@code ProjectStorage} state saved by
     * plugin versions &lt; 1.42, which didn't have a {@code layoutMode} field.
     */
    public static LayoutMode fromLegacyVisibility(boolean notebook, boolean chapter, boolean note) {
        if (notebook && chapter && note) {
            return FULL;
        }
        if (!notebook && chapter && note) {
            return CHAPTER_PLUS;
        }
        if (!notebook && !chapter && note) {
            return NOTE_PLUS;
        }
        return CONTENT_ONLY;
    }
}
