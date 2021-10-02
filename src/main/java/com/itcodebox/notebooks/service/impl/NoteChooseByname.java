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
 * @Author LittleRed
 * @Date 2021/10/2 14:45
 * @Description TODO
 * @Since version-1.0
 */
public class NoteChooseByname extends SearchRecordServiceImpl implements ChooseByNameContributor {
    
    public static List<NoteNavigationItem> list = new ArrayList<>();
    public static List<SearchRecord> records;

    public NoteChooseByname() {
        // 查询所有笔记
        records = searchKeywords(null, SearchMode.None);
        list.clear();
        for (int i = 0; i < records.size(); i++) {
            list.add(new NoteNavigationItem(records.get(i).toString(), null));
        }
    }
    /**
     * @description: 提供全部选项
     * @return java.lang.String[]
     */
    // @NotNull
    @Override
    public String[] getNames(Project project, boolean b) {
        String[] strings = list.parallelStream().map(NoteNavigationItem::getValue).toArray(String[]::new);
        return strings;
    }

    
    /**
     * @description: 匹配到符合的项
     * @return com.intellij.navigation.NavigationItem[]
     */
    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String localPatternName, Project project, boolean b) {
        NavigationItem[] navigationItems = list.parallelStream().filter(
                p -> p.getValue().equals(name)
        )
                .toArray(NavigationItem[]::new);
        return navigationItems;
    }
}

