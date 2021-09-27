package com.itcodebox.notebooks.ui.toolsettings;

import com.intellij.openapi.project.Project;
import com.intellij.util.messages.Topic;

import java.awt.*;

/**
 * @author LeeWyatt
 */
public interface AppSettingsChangedListener {
    /**
     * 其他的App设置,在使用前会先判断, 暂时只有标题可扩展, 需要通知其他的project
     */
    Topic<AppSettingsChangedListener> TOPIC = Topic.create("SettingsChanged", AppSettingsChangedListener.class);

    /**
     * 当item被选择时, 如果数据过长, 设置是否完整显示单元格里的数据
     * @param expandable true扩展显示, false不显示
     */
    public void onSetItemExpandable(boolean expandable);

    public void onSetCustomFont(Font font);

    public void onSetReadOnlyMode(Project eventProject, boolean isReadOnly);

    public void onSetShowFocusBorder(boolean show);
}
