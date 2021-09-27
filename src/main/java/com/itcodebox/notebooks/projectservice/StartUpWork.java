package com.itcodebox.notebooks.projectservice;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.startup.StartupActivity;
import com.itcodebox.notebooks.utils.ImportUtil;
import org.jetbrains.annotations.NotNull;

public class StartUpWork implements StartupActivity{


    @Override
    public void runActivity(@NotNull Project project) {
        @NotNull Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects.length == 1) {
            ImportUtil.publishReadOnlyMode(project,false);
        }
    }
}