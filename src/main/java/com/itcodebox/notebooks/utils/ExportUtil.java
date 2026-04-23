package com.itcodebox.notebooks.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.entity.ImageRecord;
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
    private static final Logger LOG = Logger.getInstance(ExportUtil.class);
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
                    new Task.Backgroundable(project, "Export JSON and Image Files", true) {
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            indicator.setText("Export images...");
                            //导出图片
                            try {
                                CustomFileUtil.copyDirectory(PluginConstant.IMAGE_DIRECTORY_PATH.toFile(), dirPath.resolve("notebook_" + fileTimeStr + ".assets").toFile());
                            } catch (IOException e) {
                                LOG.warn("Failed to copy image directory during JSON export", e);
                            }
                            indicator.checkCanceled();
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
                                LOG.warn("Failed to build URL for exported JSON file", e);
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
            ProgressManager.getInstance().run(new Task.Backgroundable(project, "Export markdown file", true) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    indicator.setText("Export images...");
                    String title = file.getName();
                    //1.导出图片
                    String assetsFileName = title + "_" + fileTimeStr + ".assets";
                    indicator.checkCanceled();
                    try {
                        List<String> imagePathList = NoteServiceImpl.getInstance().getImageRecordsByNotebookId(notebook.getId());
                        CustomFileUtil.exportImagesToDirectory(imagePathList, dirPath.resolve(assetsFileName).toFile());
                    } catch (IOException e) {
                        LOG.warn("Failed to export images during Markdown export", e);
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
                        LOG.warn("Failed to build URL for exported Markdown file", e);
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

    /**
     * Export every notebook/chapter/note as a Markdown file tree under {@code dirPath}.
     * Structure:
     * <pre>
     *   dirPath/
     *     &lt;notebook&gt;/
     *       &lt;chapter&gt;/
     *         &lt;note&gt;.md
     *       _assets/
     *         image files referenced by notes in this notebook
     * </pre>
     * This is the "Legacy Export" safety net: gives users a portable,
     * human-readable, Obsidian-friendly copy of all their notes — so they
     * can migrate off the plugin whenever they want.
     */
    public static void exportMarkdownTree(Project project, Path dirPath, String fileTimeStr) {
        File file = dirPath.toFile();
        if (!file.exists() || !file.isDirectory()) {
            NotifyUtil.showErrorNotification(
                    project,
                    PluginConstant.NOTIFICATION_ID_IMPORT_EXPORT,
                    message("notify.exportError.title"),
                    message("notify.exportError.message"));
            return;
        }
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Export Markdown tree", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(false);
                NotebookServiceImpl notebookService = NotebookServiceImpl.getInstance();
                ChapterServiceImpl chapterService = ChapterServiceImpl.getInstance();
                NoteServiceImpl noteService = NoteServiceImpl.getInstance();
                List<Notebook> notebooks = notebookService.findAll();
                int total = Math.max(notebooks.size(), 1);
                int done = 0;
                int noteCount = 0;
                for (Notebook notebook : notebooks) {
                    indicator.checkCanceled();
                    indicator.setText("Exporting " + notebook.getTitle());
                    File notebookDir = dirPath.resolve(sanitizeFilename(notebook.getTitle())).toFile();
                    if (!notebookDir.exists() && !notebookDir.mkdirs()) {
                        LOG.warn("Failed to create notebook directory: " + notebookDir);
                        continue;
                    }
                    File assetsDir = new File(notebookDir, "_assets");
                    try {
                        List<String> imagePathList = noteService.getImageRecordsByNotebookId(notebook.getId());
                        if (!imagePathList.isEmpty()) {
                            if (!assetsDir.exists()) {
                                assetsDir.mkdirs();
                            }
                            CustomFileUtil.exportImagesToDirectory(imagePathList, assetsDir);
                        }
                    } catch (IOException e) {
                        LOG.warn("Failed to export images for notebook " + notebook.getTitle(), e);
                    }
                    List<Chapter> chapters = chapterService.findAllByNotebookId(notebook.getId());
                    for (Chapter chapter : chapters) {
                        indicator.checkCanceled();
                        File chapterDir = new File(notebookDir, sanitizeFilename(chapter.getTitle()));
                        if (!chapterDir.exists() && !chapterDir.mkdirs()) {
                            LOG.warn("Failed to create chapter directory: " + chapterDir);
                            continue;
                        }
                        List<Note> notes = noteService.findAllByChapterId(chapter.getId());
                        for (Note note : notes) {
                            writeNoteAsMarkdown(note, chapterDir, "../_assets/");
                            noteCount++;
                        }
                    }
                    done++;
                    indicator.setFraction((double) done / total);
                }
                try {
                    URL dirUrl = dirPath.toUri().toURL();
                    NotifyUtil.showInfoNotification(
                            project,
                            PluginConstant.NOTIFICATION_ID_IMPORT_EXPORT,
                            message("notify.exportSucceed.markdownTree.title"),
                            message("notify.exportSucceed.markdownTree.message", noteCount)
                                    + "<a href='" + dirUrl + "'>" + dirUrl + "</a>");
                } catch (MalformedURLException e) {
                    LOG.warn("Failed to build URL for exported Markdown tree", e);
                }
            }
        });
    }

    private static void writeNoteAsMarkdown(Note note, File chapterDir, String assetsRelativePrefix) {
        String baseName = sanitizeFilename(note.getTitle() == null || note.getTitle().isBlank()
                ? ("note-" + note.getId())
                : note.getTitle());
        File outFile = uniqueFile(chapterDir, baseName, ".md");
        StringBuilder md = new StringBuilder(512);
        md.append("---\n");
        md.append("title: ").append(yamlEscape(note.getTitle())).append('\n');
        if (note.getType() != null && !note.getType().isBlank()) {
            md.append("type: ").append(yamlEscape(note.getType())).append('\n');
        }
        if (note.getSource() != null && !note.getSource().isBlank()) {
            md.append("source: ").append(yamlEscape(note.getSource())).append('\n');
        }
        if (note.getOffsetStart() != 0 || note.getOffsetEnd() != 0) {
            md.append("offset_start: ").append(note.getOffsetStart()).append('\n');
            md.append("offset_end: ").append(note.getOffsetEnd()).append('\n');
        }
        if (note.getCreateTime() != null) {
            md.append("create_time: ").append(note.getCreateTime()).append('\n');
        }
        if (note.getUpdateTime() != null) {
            md.append("update_time: ").append(note.getUpdateTime()).append('\n');
        }
        md.append("---\n\n");
        md.append("# ").append(note.getTitle() == null ? "" : note.getTitle()).append("\n\n");
        if (note.getDescription() != null && !note.getDescription().isBlank()) {
            md.append(note.getDescription()).append("\n\n");
        }
        if (note.getContent() != null && !note.getContent().isBlank()) {
            String fence = (note.getType() != null && !note.getType().isBlank()) ? note.getType() : "";
            md.append("```").append(fence).append('\n');
            md.append(note.getContent());
            if (!note.getContent().endsWith("\n")) {
                md.append('\n');
            }
            md.append("```\n\n");
        }
        String imageRecordsJson = note.getImageRecords();
        if (imageRecordsJson != null && !imageRecordsJson.isBlank()) {
            List<ImageRecord> records = ImageRecordUtil.convertToList(imageRecordsJson);
            for (ImageRecord rec : records) {
                if (rec == null || rec.getImagePath() == null || rec.getImagePath().isBlank()) {
                    continue;
                }
                String altText = rec.getImageTitle() != null ? rec.getImageTitle() : rec.getImagePath();
                md.append("![").append(altText.replace("]", "\\]")).append("](")
                        .append(assetsRelativePrefix).append(rec.getImagePath()).append(")\n");
                if (rec.getImageDesc() != null && !rec.getImageDesc().isBlank()) {
                    md.append("<!-- ").append(rec.getImageDesc().replace("-->", "-- >")).append(" -->\n");
                }
                md.append('\n');
            }
        }
        try (FileOutputStream os = new FileOutputStream(outFile)) {
            os.write(md.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOG.warn("Failed to write note markdown file: " + outFile, e);
        }
    }

    private static File uniqueFile(File dir, String baseName, String ext) {
        File candidate = new File(dir, baseName + ext);
        int i = 1;
        while (candidate.exists()) {
            candidate = new File(dir, baseName + "-" + i + ext);
            i++;
        }
        return candidate;
    }

    private static String sanitizeFilename(String raw) {
        if (raw == null || raw.isBlank()) {
            return "untitled";
        }
        // Strip filesystem-reserved characters + control chars; collapse whitespace; trim dots.
        String s = raw.replaceAll("[\\\\/:*?\"<>|\\x00-\\x1F]", "_").trim();
        s = s.replaceAll("\\s+", " ");
        // Avoid trailing dots/spaces (Windows-hostile).
        s = s.replaceAll("[. ]+$", "");
        if (s.isEmpty()) {
            return "untitled";
        }
        // Cap length — some filesystems choke past 255 bytes; be conservative.
        if (s.length() > 120) {
            s = s.substring(0, 120);
        }
        return s;
    }

    private static String yamlEscape(String s) {
        if (s == null) {
            return "";
        }
        // Quote if contains chars that would require escaping in a YAML bare scalar.
        if (s.contains(":") || s.contains("#") || s.contains("\"") || s.contains("'")
                || s.contains("\n") || s.startsWith("-") || s.startsWith("?") || s.startsWith("[") || s.startsWith("{")) {
            return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
        }
        return s;
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
            LOG.warn("Failed to serialize all notebooks to JSON for export", e);
        }
        return "";
    }

    private static String processToJsonString(int notebookId) {
        //1. 获取数据
        NotebookServiceImpl notebookService = NotebookServiceImpl.getInstance();
        ChapterServiceImpl chapterService = ChapterServiceImpl.getInstance();
        NoteServiceImpl noteService = NoteServiceImpl.getInstance();
        LinkedHashMap<Notebook, LinkedHashMap<Chapter, List<Note>>> map = new LinkedHashMap<>(1);
        Notebook notebook = notebookService.findById(notebookId);
        if (notebook != null) {
            List<Chapter> chapterList = chapterService.findAllByNotebookId(notebook.getId());
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
            LOG.warn("Failed to serialize single notebook to JSON for export", e);
        }
        return "";
    }
}
