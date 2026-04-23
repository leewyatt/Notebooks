package com.itcodebox.notebooks.ui.notify;

import com.intellij.ide.actions.RevealFileAction;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.ui.dialog.ImportErrorDialog;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsConfigurable;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
import java.nio.file.Path;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class NotifyUtil {

    public static void showNotification(Project project, String displayId, String title, String content, NotificationType type) {
        Notification notification =  new Notification(PluginConstant.DEFAULT_NOTIFICATION_GROUP_ID,title, content, type);
        notification.addAction(new EmptyAction());
        Notifications.Bus.notify(notification, project);
    }

    public static void showInfoNotification(Project project, String displayId, String title, String message) {
        showNotification(project, displayId, title, message, NotificationType.INFORMATION);
    }

    /**
     * Info-level notification with a "Show in Finder/Explorer/Files" action
     * button that reveals {@code pathToReveal} in the OS file manager.
     *
     * <p>Previously export notifications embedded {@code <a href="file://…">}
     * links expecting a {@code NotificationListener.UrlOpeningListener} to
     * handle clicks. That listener was declared but never attached — the
     * links rendered but did nothing. {@link NotificationAction} is the
     * modern (2022.3+) equivalent and works reliably.
     */
    public static void showInfoNotificationWithReveal(Project project, String displayId, String title,
                                                      String content, Path pathToReveal) {
        Notification notification = new Notification(PluginConstant.DEFAULT_NOTIFICATION_GROUP_ID,
                title, content, NotificationType.INFORMATION);
        if (pathToReveal != null && RevealFileAction.isSupported()) {
            String label = message("notify.action.showInFileManager.text",
                    RevealFileAction.getFileManagerName());
            notification.addAction(NotificationAction.createSimple(label,
                    () -> RevealFileAction.openFile(pathToReveal.toFile())));
        }
        Notifications.Bus.notify(notification, project);
    }

    public static void showWarningNotification(Project project, String displayId, String title, String message) {
        showNotification(project, displayId, title, message, NotificationType.WARNING);
    }

    public static void showErrorNotification(Project project, String displayId, String title, String message) {
        showNotification(project, displayId, title, message, NotificationType.ERROR);
    }


    public static void showTemplateErrorNotification(Project project, String displayId, String title, String content) {
        //Notification notification = getNotifyGroup(displayId)
        //        .createNotification(title, content, NotificationType.ERROR, null);
        Notification notification =  new Notification(PluginConstant.DEFAULT_NOTIFICATION_GROUP_ID,title, content, NotificationType.ERROR);
        notification.addAction(new DumbAwareAction(message("notify.action.setTemplate.text")) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, AppSettingsConfigurable.class);
            }
            
            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread ()
            {
                return ActionUpdateThread.BGT;
            }
        });
        Notifications.Bus.notify(notification, project);
    }
//com.google.gson.stream.MalformedJsonException: Expected name at line 388 column 1186 path $[2][1][0][1][1].imageRecords
    public static void showErrorNotification(Project project, String displayId, String title, String message, String errorMessage) {
        Notification notification =  new Notification(PluginConstant.DEFAULT_NOTIFICATION_GROUP_ID,title, message, NotificationType.ERROR);
        notification.addAction(new DumbAwareAction(message("notify.action.copyErrorMsg.text")) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                PluginConstant.CLIPBOARD.setContents(new StringSelection(errorMessage), null);
            }
            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread ()
            {
                return ActionUpdateThread.BGT;
            }
        });
        Notifications.Bus.notify(notification, project);
    }

    public static void showImportErrorNotification(Project project, String displayId, String title, String message, String errorMessage) {
        Notification notification =  new Notification(PluginConstant.DEFAULT_NOTIFICATION_GROUP_ID,title, message, NotificationType.ERROR);
        notification.addAction(new DumbAwareAction(message("notify.import.jsonException.button.detail")) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                new ImportErrorDialog(errorMessage).show();
            }
            
            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread ()
            {
                return ActionUpdateThread.BGT;
            }
        });
        Notifications.Bus.notify(notification, project);
    }

}
