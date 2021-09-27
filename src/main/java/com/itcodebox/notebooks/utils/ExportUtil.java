package com.itcodebox.notebooks.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.entity.Notebook;
import com.itcodebox.notebooks.service.impl.ChapterServiceImpl;
import com.itcodebox.notebooks.service.impl.NoteServiceImpl;
import com.itcodebox.notebooks.service.impl.NotebookServiceImpl;
import com.itcodebox.notebooks.ui.notify.NotifyUtil;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsState;
import groovy.lang.GroovyRuntimeException;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class ExportUtil {
    public static final int EXPORT_ALL = Integer.MIN_VALUE;

    /**
     * 导出JSON格式的数据和图片文件
     * Integer.MIN_VALUE 代表全部输出数据
     * 其余时候会查找id,
     */
    public static void exportJsonAndImage(Project project, Path dirPath, String fileTimeStr, int notebookId) {

        File file = dirPath.toFile();
        if (file.exists() && file.isDirectory()) {
            //导出JSON,设置为不可取消
            ProgressManager.getInstance().run(
                    new Task.Backgroundable(project, "Export JSON and Image Files", false) {
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            indicator.setText("Export images...");
                            //导出图片
                            try {
                                CustomFileUtil.copyDirectory(PluginConstant.IMAGE_DIRECTORY_PATH.toFile(), dirPath.resolve("notebook_" + fileTimeStr + ".assets").toFile());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            indicator.setText("Export data...");
                            //导出JSON
                            Path jsonPath = dirPath.resolve("notebook_" + fileTimeStr + ".json");
                            try (FileOutputStream os = new FileOutputStream(jsonPath.toFile())) {
                                String jsonStr;
                                if (notebookId == EXPORT_ALL) {
                                    jsonStr = ExportUtil.processToJsonString();
                                } else {
                                    jsonStr = ExportUtil.processToJsonString(notebookId);
                                }
                                os.write(jsonStr.getBytes(StandardCharsets.UTF_8));
                            } catch (IOException exception) {
                                // 通知: 导出失败: IO 异常
                                NotifyUtil.showErrorNotification(
                                        project,
                                        PluginConstant.NOTIFICATION_ID_IMPORT_EXPORT,
                                        message("notify.exportFailed.title"),
                                        message("notify.exportFailed.messageIO"));
                                return;
                            }

                            // 通知: 导出成功: 因为前面没有抛出异常 能运行到这里说明保存已经成功
                            try {
                                URL jsonUrl = jsonPath.toUri().toURL();
                                NotifyUtil.showInfoNotification(
                                        project,
                                        PluginConstant.NOTIFICATION_ID_IMPORT_EXPORT,
                                        message("notify.exportSucceed.json.title"),
                                        message("notify.exportSucceed.message") + "<a href='" + jsonUrl + "'>" + jsonUrl + "</a>");
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }

                        }
                    }
            );
        } else {
            // 通知: 导出失败文件没有找到或者创建成功通知
            NotifyUtil.showErrorNotification(
                    project,
                    PluginConstant.NOTIFICATION_ID_IMPORT_EXPORT,
                    message("notify.exportError.title"),
                    message("notify.exportError.message"));

        }
    }

    public static void exportMarkdownFile(Project project, Path dirPath, Notebook notebook, String fileTimeStr) {
        File file = dirPath.toFile();
        if (file.exists() && file.isDirectory()) {
            ProgressManager.getInstance().run(new Task.Backgroundable(project, "Export markdown file", false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    indicator.setText("Export images...");
                    String title = file.getName();
                    //1.导出图片
                    String assetsFileName = title + "_" + fileTimeStr + ".assets";
                    try {
                        List<String> imagePathList = NoteServiceImpl.getInstance().getImageRecordsByNotebookId(notebook.getId());
                        CustomFileUtil.exportImagesToDirectory(imagePathList, dirPath.resolve(assetsFileName).toFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    indicator.setText("Export data...");
                    Path mdPath = dirPath.resolve(title + "_" + fileTimeStr + ".md");
                    try (FileOutputStream os = new FileOutputStream(mdPath.toFile())) {
                        //关于字符集, SQLite默认UTF-8 防止乱码...
                        try {
                            os.write(ExportUtil.processToMarkdownString(notebook, assetsFileName).getBytes(StandardCharsets.UTF_8));
                        } catch (GroovyRuntimeException | IOException | ClassNotFoundException e) {
                            // 通知: 导出失败: 模板错误导致异常
                            NotifyUtil.showTemplateErrorNotification(
                                    project,
                                    PluginConstant.NOTIFICATION_ID_IMPORT_EXPORT,
                                    message("notify.exportFailed.title"),
                                    message("notify.exportFailed.messageTemplate"));
                            return;
                        }
                    } catch (IOException exception) {
                        // 通知: 导出失败: IO 异常
                        NotifyUtil.showErrorNotification(
                                project,
                                PluginConstant.NOTIFICATION_ID_IMPORT_EXPORT,
                                message("notify.exportFailed.title"),
                                message("notify.exportFailed.messageIO"));
                        return;
                    }

                    // 通知: 导出成功: 因为前面没有抛出异常 能运行到这里说明保存已经成功
                    try {
                        URL mdUrl = mdPath.toUri().toURL();
                        NotifyUtil.showInfoNotification(
                                project,
                                PluginConstant.NOTIFICATION_ID_IMPORT_EXPORT,
                                message("notify.exportSucceed.markdown.title"),
                                message("notify.exportSucceed.message") + "<a href='" + mdUrl + "'>" + mdUrl + "</a>");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            // 通知: 导出失败文件没有找到或者创建成功通知
            NotifyUtil.showErrorNotification(
                    project,
                    PluginConstant.NOTIFICATION_ID_IMPORT_EXPORT,
                    message("notify.exportError.title"),
                    message("notify.exportError.message"));

        }

    }

    private static String processToMarkdownString(Notebook notebook, String assetsFileName) throws GroovyRuntimeException, IOException, ClassNotFoundException {
        LinkedHashMap<Chapter, List<Note>> map = new LinkedHashMap<>();
        Integer notebookId = notebook.getId();
        List<Chapter> chapters = ChapterServiceImpl.getInstance().findAllByNotebookId(notebookId);
        for (Chapter chapter : chapters) {
            List<Note> notes = NoteServiceImpl.getInstance().findAllByChapterId(chapter.getId());
            map.put(chapter, notes);
        }
        HashMap<String, Object> mapResult = new HashMap<>(32);
        mapResult.put("notebookTitle", notebook.getTitle());
        mapResult.put("assetsFileName", assetsFileName + File.separator);
        mapResult.put("dataMap", map);

        StringWriter sw = new StringWriter();
        Template template = new SimpleTemplateEngine().createTemplate(AppSettingsState.getInstance().markdownTemplate132);
        Writer writer = template.make(mapResult).writeTo(sw);
        writer.close();
        return sw.toString();
    }

    private static String processToJsonString() {
        //1. 获取数据
        NotebookServiceImpl notebookService = NotebookServiceImpl.getInstance();
        ChapterServiceImpl chapterService = ChapterServiceImpl.getInstance();
        NoteServiceImpl noteService = NoteServiceImpl.getInstance();
        LinkedHashMap<Notebook, LinkedHashMap<Chapter, List<Note>>> map = new LinkedHashMap<>(256);
        List<Notebook> notebookList = notebookService.findAll();
        for (Notebook notebook : notebookList) {
            Integer notebookId = notebook.getId();
            List<Chapter> chapterList = chapterService.findAllByNotebookId(notebookId);
            LinkedHashMap<Chapter, List<Note>> noteMap = new LinkedHashMap<>(256);
            for (Chapter chapter : chapterList) {
                List<Note> noteList = noteService.findAllByChapterId(chapter.getId());
                noteMap.put(chapter, noteList);
            }
            map.put(notebook, noteMap);
        }
        //2. 把对象转成JSON字符串
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String processToJsonString(int notebookId) {
        //1. 获取数据
        NotebookServiceImpl notebookService = NotebookServiceImpl.getInstance();
        ChapterServiceImpl chapterService = ChapterServiceImpl.getInstance();
        NoteServiceImpl noteService = NoteServiceImpl.getInstance();
        LinkedHashMap<Notebook, LinkedHashMap<Chapter, List<Note>>> map = new LinkedHashMap<>(1);
        List<Notebook> notebookList = Collections.singletonList(notebookService.findById(notebookId));
        for (Notebook notebook : notebookList) {
            Integer tempNotebookId = notebook.getId();
            List<Chapter> chapterList = chapterService.findAllByNotebookId(tempNotebookId);
            LinkedHashMap<Chapter, List<Note>> noteMap = new LinkedHashMap<>(50);
            for (Chapter chapter : chapterList) {
                List<Note> noteList = noteService.findAllByChapterId(chapter.getId());
                noteMap.put(chapter, noteList);
            }
            map.put(notebook, noteMap);
        }
        //2. 把对象转成JSON字符串
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
