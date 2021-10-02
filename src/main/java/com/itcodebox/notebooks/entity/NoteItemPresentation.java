package com.itcodebox.notebooks.entity;

import com.intellij.navigation.ItemPresentation;
import com.itcodebox.notebooks.service.impl.NoteChooseByname;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @Author LittleRed
 * @Date 2021/10/2 15:40
 * @Description TODO
 * @Since version-1.0
 */
public class NoteItemPresentation implements ItemPresentation {

    private String name;
    public NoteItemPresentation(String name) {
        this.name = name;
    }

    /**
     * @description: 搜索结果最终显示
     * @return java.lang.String
     */
    @Nullable
    @Override
    public String getPresentableText() {
        return name;
    }

    /**
     * @description: 搜索结果的辅助说明
     * @return java.lang.String
     */
    @Nullable
    @Override
    public String getLocationString() {
        SearchRecord searchRecord = NoteChooseByname.records.parallelStream().filter(record -> record.toString().equals(name))
                .findFirst().get();
        String description = searchRecord.getDescription();
        return description;
    }

    /**
     * @description: 搜索结果的图标
     * @return javax.swing.Icon
     */
    @Nullable
    @Override
    public Icon getIcon(boolean b) {
        return null;
    }
}
