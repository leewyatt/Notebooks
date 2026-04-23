package com.itcodebox.notebooks.service.impl;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.itcodebox.notebooks.entity.NoteNavigationItem;
import com.itcodebox.notebooks.entity.SearchMode;
import com.itcodebox.notebooks.entity.SearchRecord;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Contributor backing IDE "Go To Symbol" (Ctrl+Alt+Shift+N) for notebook notes.
 *
 * <p>Cached snapshots of the currently known notes are held in
 * {@code volatile} fields that always reference <b>immutable</b> lists. A new
 * constructor call atomically swaps in a fresh snapshot; readers never see a
 * half-built state. Previously these were public mutable {@code ArrayList}s
 * shared across all projects and threads — rapid Go-To-Symbol invocations plus
 * multi-project sessions would read a list while another thread was
 * {@code list.clear()}-ing it.
 */
public class NoteChooseByname extends SearchRecordServiceImpl implements ChooseByNameContributor {

    /** Snapshots are immutable; only the references are ever reassigned. */
    private static volatile List<NoteNavigationItem> list = List.of();
    private static volatile List<SearchRecord> records = List.of();

    public NoteChooseByname() {
        List<SearchRecord> freshRecords = searchKeywords(null, SearchMode.None);
        if (freshRecords == null) {
            freshRecords = List.of();
        }
        List<NoteNavigationItem> freshList = new ArrayList<>(freshRecords.size());
        for (SearchRecord r : freshRecords) {
            freshList.add(new NoteNavigationItem(r.toString(), null));
        }
        records = List.copyOf(freshRecords);
        list = List.copyOf(freshList);
    }

    /**
     * Immutable snapshot of the cached search records. Safe to iterate
     * concurrently with constructor invocations; readers never see a partial
     * rebuild because the assignment in the constructor is atomic on a
     * {@code volatile} field and the target is an immutable list.
     */
    public static List<SearchRecord> getRecords() {
        return records;
    }

    @Override
    public String[] getNames(Project project, boolean b) {
        return list.parallelStream()
                .map(NoteNavigationItem::getValue)
                .toArray(String[]::new);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String localPatternName, Project project, boolean b) {
        return list.parallelStream()
                .filter(p -> p.getValue().equals(name))
                .toArray(NavigationItem[]::new);
    }
}
