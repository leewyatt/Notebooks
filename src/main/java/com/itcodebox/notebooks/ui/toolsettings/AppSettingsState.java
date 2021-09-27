package com.itcodebox.notebooks.ui.toolsettings;// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @author LeeWyatt
 */
@State(
        name = "com.itcodebox.notebooks.ui.toolsettings.AppSettingsState",
        storages = {@Storage("NotebooksSettingsPlugin.xml")}
)
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

    /**
     * 内容以及代码的字体名称
     */
    public String customFontName = Font.MONOSPACED;
    /**
     * 内容以及代码的字体大小
     */
    public int customFontSize = 18;

    /**
     * 缩略图尺寸398x398
     */
    public int thumbMaxSize = 398;

    /**
     * 打开toolWindow时是否恢复最后一次的选择
     */
    public boolean restoreSelected = true;

    /**
     * 如果没有选择文字是否可以右键添加到笔记
     */
    public boolean addMenuItemVisible = false;

    /**
     * 当item太长时,能否超出表格/列表进行显示
     */
    public boolean itemExpandable = true;

    /**
     *  升级之后, 修改下存储的变量名,这样应该可以更新一次模板
     */
    public String markdownTemplate132 = MARKDOWN_GROOVY_TEMPLATE;

    /**
     * 只读模式。true 开启，false 关闭
     */
    public boolean readOnlyMode = false;

    /**
     * Table选中时，是否显示边框
     */
    public boolean showFocusBorder = true;


    ///**
    // * 正在进行导入或者导出操作
    // */
    //AtomicBoolean isImportOrExport = new AtomicBoolean(false);


    public static final String MARKDOWN_GROOVY_TEMPLATE = "<%\n" +
            "println(\"# \"+notebookTitle)\n" +
            "println(\"[TOC]\")\n" +
            "dataMap.eachWithIndex{item,chapterIndex->\n" +
            "    println(\"## Chapter \"+(chapterIndex+1)+\" ${item.key.title}\")\n" +
            "    item.value.eachWithIndex{note,noteIndex->\n" +
            "            println(\"### \"+(noteIndex+1)+\" ${note.title}\")\n" +
            "                    if(note.description?.trim()){\n" +
            "                        println(note.description)\n" +
            "                    }\n" +
            "                    if(note.content?.trim()){\n" +
            "                        if(note.type?.trim()){\n" +
            "                            println(\"```${note.type}\")\n" +
            "                        }else{\n" +
            "                            println(\"```\")\n" +
            "                        }\n" +
            "                        println(note.content)\n" +
            "                        println(\"```\")\n" +
            "                    }\n" +
            "                    note.imageRecordList.eachWithIndex{imageRecord,imageRecordIndex->\n" +
            "                        if(imageRecord.imageTitle?.trim()){\n" +
            "                            println(\"Image \"+(imageRecordIndex+1)+\" ${imageRecord.imageTitle}\")\n" +
            "                        }else{\n" +
            "                            println(\"Image \"+(imageRecordIndex+1)+\"\")\n" +
            "                        }\n" +
            "                        if(imageRecord.imageDesc?.trim()){\n" +
            "                            println(imageRecord.imageDesc)\n" +
            "                        }\n" +
            "                        if(imageRecord.imagePath?.trim()){\n" +
            "                            println(\"![${imageRecord.imageTitle}](\"+assetsFileName+imageRecord.imagePath+\")\")\n" +
            "                        }\n" +
            "\n" +
            "                    }\n" +
            "                    if(note.source?.trim()){\n" +
            "                        println(\"[Source Path](\"+note.source+\")\")\n" +
            "                    }\n" +
            "        }\n" +
            "}\n" +
            "%>";

    //public static final String MARKDOWN_GROOVY_TEMPLATE = "<%\n" +
    //        "println(\"# \"+notebookTitle)\n" +
    //        "println(\"[TOC]\")\n" +
    //        "dataMap.eachWithIndex{item,chapterIndex->\n" +
    //        "    println(\"## Chapter \"+(chapterIndex+1)+\" ${item.key.title}\")\n" +
    //        "    item.value.eachWithIndex{note,noteIndex->\n" +
    //        "            println(\"### \"+(noteIndex+1)+\" ${note.title}\")\n" +
    //        "                    if(note.description?.trim()){\n" +
    //        "                        println(note.description)\n" +
    //        "                    }\n" +
    //        "                    if(note.content?.trim()){\n" +
    //        "                        if(note.type?.trim()){\n" +
    //        "                            println(\"```${note.type}\")\n" +
    //        "                        }else{\n" +
    //        "                            println(\"```\")\n" +
    //        "                        }\n" +
    //        "                        println(note.content)\n" +
    //        "                        println(\"```\")\n" +
    //        "                    }\n" +
    //        "                    if(note.source?.trim()){\n" +
    //        "                        println(\"[Source Path](\"+note.source+\")\")\n" +
    //        "                    }\n" +
    //        "        }\n" +
    //        "}\n" +
    //        "%>";

    /**
     * 使用指南的索引
     */
    public int tipIndex = 0;

    public AppSettingsState() {
    }

    public static AppSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(AppSettingsState.class);
    }

    @Nullable
    @Override
    public AppSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}
