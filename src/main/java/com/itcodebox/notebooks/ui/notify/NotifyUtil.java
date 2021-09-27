package com.itcodebox.notebooks.ui.notify;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.ui.dialog.ImportErrorDialog;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsConfigurable;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class NotifyUtil {

    private static final NotificationListener.UrlOpeningListener URL_OPENING_LISTENER = new NotificationListener.UrlOpeningListener(true);

    //203的写法

    public static void showNotification(Project project, String displayId, String title, String content, NotificationType type) {
        Notification notification =  new Notification(PluginConstant.DEFAULT_NOTIFICATION_GROUP_ID,title, content, type);
        notification.setListener(URL_OPENING_LISTENER);
        Notifications.Bus.notify(notification, project);
    }

    public static void showInfoNotification(Project project, String displayId, String title, String message) {
        showNotification(project, displayId, title, message, NotificationType.INFORMATION);
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
        });
        Notifications.Bus.notify(notification, project);
    }

}
