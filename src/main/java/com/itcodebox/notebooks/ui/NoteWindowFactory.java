package com.itcodebox.notebooks.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileElement;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.DumbAwareToggleAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.tools.SimpleActionGroup;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.projectservice.NotebooksUIManager;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.ui.dialog.ClearCacheDialog;
import com.itcodebox.notebooks.ui.dialog.TipForUsingDialog;
import com.itcodebox.notebooks.ui.panes.MainPanel;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsChangedListener;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsState;
import com.itcodebox.notebooks.utils.CustomFileUtil;
import com.itcodebox.notebooks.utils.ExportUtil;
import com.itcodebox.notebooks.utils.ImportUtil;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class NoteWindowFactory implements ToolWindowFactory, DumbAware {
    private Project project;
    private MainPanel mainPanel;
    private final AppSettingsState appSettingsState = AppSettingsState.getInstance();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.project = project;
        this.mainPanel = new MainPanel(project, toolWindow);
        NotebooksUIManager uiManger = project.getService(NotebooksUIManager.class);
        uiManger.setMainPanel(mainPanel);
        // 获取内容工厂的实例
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        // 获取用于toolWindow显示的内容
        Content content = contentFactory.createContent(mainPanel, "", false);
        //给toolWindow设置内容
        toolWindow.getContentManager().addContent(content);
        //标题栏上添加 动作按钮
        toolWindow.setTitleActions(List.of(
                initTipsAction(),
                initRefreshAction(),
                initExpandableAction()
        ));
        // 强制转换
        ToolWindowEx tw = (ToolWindowEx) toolWindow;
        //齿轮上添加 动作按钮
        SimpleActionGroup gearActions = new SimpleActionGroup();
        gearActions.add(initActionExportJson());
        gearActions.add(initActionImportJson());
        gearActions.add(new Separator());
        gearActions.add(initActionClearCache());
        tw.setAdditionalGearActions(gearActions);

        Disposer.register(project, content);

    }

    private DumbAwareAction initActionClearCache() {
        return new DumbAwareAction(message("cacheDialog.title"),"",PluginIcons.Clear) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (PluginConstant.IsClearing.get()) {
                    Messages.showInfoMessage(message("cacheDialog.background.indicator.text"),message("cacheDialog.notify.busy"));
                }else{
                    new ClearCacheDialog(project).show();
                }

            }
        };
    }

    @NotNull
    private DumbAwareAction initRefreshAction() {
        return new DumbAwareAction(message("mainPanel.action.refresh.text"), "", AllIcons.Actions.Refresh) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                //刷新Table
                ApplicationManager.getApplication().getMessageBus().syncPublisher(RecordListener.TOPIC)
                        .onRefresh();
            }
        };
    }

    @NotNull
    private DumbAwareAction initTipsAction() {
        return new DumbAwareAction(message("mainPanel.action.tips.text"), "", PluginIcons.Tip) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                new TipForUsingDialog().show();
            }
        };
    }

    private DumbAwareAction initActionExportJson() {
        return new DumbAwareAction(message("mainPanel.action.exportJson.text"), "", AllIcons.ToolbarDecorator.Export) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                //刷新Table (导出前,同步刷新下数据, 看下其他工具, 有没有修改数据)
                ApplicationManager.getApplication().getMessageBus().syncPublisher(RecordListener.TOPIC)
                        .onRefresh();

                if (mainPanel.getNotebookTable().getRowCount() < 1) {
                    Messages.showMessageDialog(project, message("mainPanel.action.exportJson.empty.message"), message("mainPanel.action.exportJson.empty.title"), Messages.getInformationIcon());
                    return;
                }
                DateTimeFormatter fileTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                String fileTimeStr = fileTimeFormatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault()));
                Path path = CustomFileUtil.choosePath(project, "Notebook_Data");
                if (path != null) {
                    ExportUtil.exportJsonAndImage(project, path, fileTimeStr, ExportUtil.EXPORT_ALL);
                }

            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                e.getPresentation().setEnabled(!AppSettingsState.getInstance().readOnlyMode);
            }
        };
    }

    private DumbAwareToggleAction initExpandableAction() {
        return new DumbAwareToggleAction(message("mainPanel.action.expandableItems.text"), "", PluginIcons.ExpandAll) {
            @Override
            public boolean isSelected(@NotNull AnActionEvent anActionEvent) {
                return appSettingsState.itemExpandable;
            }

            @Override
            public void setSelected(@NotNull AnActionEvent anActionEvent, boolean b) {
                appSettingsState.itemExpandable = b;
                ApplicationManager.getApplication()
                        .getMessageBus()
                        .syncPublisher(AppSettingsChangedListener.TOPIC)
                        .onSetItemExpandable(b);
            }
        };
    }

    private DumbAwareAction initActionImportJson() {
        return new DumbAwareAction(message("mainPanel.action.importJson.fileChooser.title"), "", AllIcons.ToolbarDecorator.Import) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                //刷新Table (导入前,同步刷新下数据, 看下其他工具, 有没有修改数据)
                ApplicationManager.getApplication().getMessageBus().syncPublisher(RecordListener.TOPIC)
                        .onRefresh();


                //文件选择器
                VirtualFile[] virtualFiles = FileChooserFactory.getInstance().createFileChooser(
                        new FileChooserDescriptor(true, false, false, false, false, false) {
                            private boolean isValidExtension(VirtualFile file) {
                                String extension = file.getExtension();
                                if (extension == null) {
                                    return false;
                                }
                                return "json".equalsIgnoreCase(extension);
                            }

                            @Override
                            public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
                                return (file.isDirectory() || isValidExtension(file)) && (showHiddenFiles || !FileElement.isFileHidden(file));
                            }

                            @Override
                            public boolean isFileSelectable(VirtualFile file) {
                                return !file.isDirectory() && isValidExtension(file);
                            }
                        },
                        project, null)
                        .choose(project);
                if (virtualFiles.length < 1) {
                    return;
                }
                VirtualFile selectedFile = virtualFiles[0];
                selectedFile.refresh(false, false);
                // 后台开始导入JSON 文件到数据库
                ImportUtil.importJsonFile(project, selectedFile);

            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                e.getPresentation().setEnabled(!AppSettingsState.getInstance().readOnlyMode);
            }
        };
    }

}
