package com.itcodebox.notebooks.ui.toolsettings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

public class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent settingsPanel;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return message("settingPage.name");
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        settingsPanel = new AppSettingsComponent();
        return settingsPanel.getSettingsPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();
        boolean modified = !settingsPanel.getCustomFontName().equals(settings.customFontName)
                || settingsPanel.getCustomFontSize() != settings.customFontSize
                || settingsPanel.getRestoreSelected() != settings.restoreSelected
                || settingsPanel.getAddMenuItemVisibleStatus() != settings.addMenuItemVisible
                || settingsPanel.getItemExpandableStatus() != settings.itemExpandable
                || settingsPanel.getShowFocusBorder() != settings.showFocusBorder
                || settingsPanel.getThumbMaxSize() != settings.thumbMaxSize
                || !Objects.equals(settingsPanel.getMarkdownTemplate(),settings.markdownTemplate132);
        return modified;
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        String newFontName = settingsPanel.getCustomFontName();
        int newFontSize = settingsPanel.getCustomFontSize();
        //1. 如果字体发生了改变,通知各个项目
        if (newFontSize != settings.customFontSize || !Objects.equals(newFontName, settings.customFontName)) {
            ApplicationManager.getApplication()
                    .getMessageBus()
                    .syncPublisher(AppSettingsChangedListener.TOPIC)
                    .onSetCustomFont(new Font(newFontName, Font.PLAIN, newFontSize));
        }
        settings.customFontName = newFontName;
        settings.customFontSize = newFontSize;

        //2. 如果item可扩展显示改变了, 通知各个项目
        boolean newExpandableStatus = settingsPanel.getItemExpandableStatus();
        if (newExpandableStatus != settings.itemExpandable) {
            ApplicationManager.getApplication()
                    .getMessageBus()
                    .syncPublisher(AppSettingsChangedListener.TOPIC)
                    .onSetItemExpandable(newExpandableStatus);
        }
        settings.itemExpandable = newExpandableStatus;
        settings.restoreSelected = settingsPanel.getRestoreSelected();
        settings.addMenuItemVisible = settingsPanel.getAddMenuItemVisibleStatus();
        settings.markdownTemplate132 = settingsPanel.getMarkdownTemplate();
        boolean newShowFocusBorder = settingsPanel.getShowFocusBorder();
        if (newShowFocusBorder != settings.showFocusBorder) {
            ApplicationManager.getApplication()
                    .getMessageBus()
                    .syncPublisher(AppSettingsChangedListener.TOPIC)
                    .onSetShowFocusBorder(newShowFocusBorder);
        }
        settings.showFocusBorder = settingsPanel.getShowFocusBorder();
        settings.thumbMaxSize = settingsPanel.getThumbMaxSize();
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settingsPanel.setCustomFontName(settings.customFontName);
        settingsPanel.setCustomFontSize(settings.customFontSize);
        settingsPanel.setRestoreSelected(settings.restoreSelected);
        settingsPanel.setAddMenuItemVisibleStatus(settings.addMenuItemVisible);
        settingsPanel.setItemExpandableStatus(settings.itemExpandable);
        settingsPanel.setMarkdownTemplate(settings.markdownTemplate132);
        settingsPanel.setShowFocusBorder(settings.showFocusBorder);
        settingsPanel.setThumbMaxSize(settings.thumbMaxSize);
    }

    @Override
    public void disposeUIResources() {
        settingsPanel = null;
    }

}
