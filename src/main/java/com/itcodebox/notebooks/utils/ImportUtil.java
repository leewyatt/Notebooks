package com.itcodebox.notebooks.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.entity.Notebook;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.impl.ChapterServiceImpl;
import com.itcodebox.notebooks.service.impl.NoteServiceImpl;
import com.itcodebox.notebooks.service.impl.NotebookServiceImpl;
import com.itcodebox.notebooks.ui.notify.NotifyUtil;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsChangedListener;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class ImportUtil {
    private static final int CHOOSE_CLOSE = -1;
    private static final int CHOOSE_OVERWRITE = 0;
    private static final int CHOOSE_SKIP = 1;
    private static final int CHOOSE_UPDATE = 2;
    private static final int CHOOSE_RENAME = 3;

    public static void importJsonFile(Project project, VirtualFile selectedFile) {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, message("notify.import.backgroundTask.title"), true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setText("Import images");
                //第一步复制图片
                String fileName = selectedFile.getName();
                String imageDir = fileName.substring(0, fileName.lastIndexOf(".")) + ".assets";
                Path imageDirPath = selectedFile.getParent().toNioPath().resolve(imageDir);
                if (imageDirPath.toFile().exists()) {
                    try {
                        CustomFileUtil.copyDirectory(imageDirPath.toFile(),PluginConstant.IMAGE_DIRECTORY_PATH.toFile());
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
                //开启只读模式
                publishReadOnlyMode(project, true);
                // 需要经常调用此方法, 确保用户点击了取消的时候,可以及时停止
                indicator.checkCanceled();
                indicator.setFraction(0.0);
                indicator.setIndeterminate(false);
                // 解析JSON成为java对象
                LinkedHashMap<Notebook, LinkedHashMap<Chapter, List<Note>>> notebookCollection = processJson(project, selectedFile);
                if (notebookCollection == null) {
                    return;
                }
                Set<Map.Entry<Notebook, LinkedHashMap<Chapter, List<Note>>>> entries = notebookCollection.entrySet();
                int size = entries.size();
                if (size == 0) {
                    return;
                }
                int index = 0;
                boolean doNotAsk = false;
                int defaultChoose = Integer.MIN_VALUE;
                NotebookServiceImpl notebookService = NotebookServiceImpl.getInstance();
                for (Map.Entry<Notebook, LinkedHashMap<Chapter, List<Note>>> entry : entries) {
                    indicator.checkCanceled();
                    Notebook notebookInJson = entry.getKey();
                    Notebook notebookInDb = notebookService.findByTitle(notebookInJson.getTitle());
                    // 如果存在,进行同名处理
                    if (notebookInDb != null) {
                        indicator.setText("Import " + notebookInDb.getTitle());
                        if (doNotAsk) {
                            if (nameConflictHandler(indicator, entry, notebookInDb, defaultChoose)) {
                                continue;
                            }
                        } else {
                            AtomicReference<UserChoose> chooseAtomicReference = new AtomicReference<>();
                            Application application = ApplicationManager.getApplication();
                            if (application.isDispatchThread()) {
                                chooseAtomicReference.set(nameConflictDialog(notebookInJson.getTitle()));
                            } else {
                                application.invokeAndWait(() -> chooseAtomicReference.set(nameConflictDialog(notebookInJson.getTitle())));
                            }
                            UserChoose userChoose = chooseAtomicReference.get();
                            if (userChoose.getExitCode() == CHOOSE_CLOSE) {
                                //终止, 算成功还是失败
                                NotifyUtil.showInfoNotification(project, PluginConstant.NOTIFICATION_ID_IMPORT_EXPORT, message("notify.import.close.title"), message("notify.import.close.message"));
                                break;
                            }

                            if (userChoose.isDoNotAsk()) {
                                doNotAsk = true;
                                defaultChoose = userChoose.getExitCode();
                            }
                            if (nameConflictHandler(indicator, entry, notebookInDb, userChoose.getExitCode())) {
                                continue;
                            }
                        }

                    } else {
                        indicator.setText("Import " + entry.getKey().getTitle());
                        addNotebookFromJson(indicator, entry);
                    }
                    double fraction = (++index) * 1.0 / size;
                    indicator.setFraction(fraction);
                }
                NotifyUtil.showInfoNotification(project, PluginConstant.NOTIFICATION_ID_IMPORT_EXPORT, message("notify.import.success.title"), message("notify.import.success.message"));

            }

            /**
             * 无论是取消, 成功,还是错误,最后都会调用到onFinished了
             * 所以在onFinished方法里刷新
             */
            @Override
            public void onFinished() {
                //刷新Table
                ApplicationManager.getApplication().getMessageBus().syncPublisher(RecordListener.TOPIC)
                        .onRefresh();
                //解除只读模式
                publishReadOnlyMode(project, false);
            }

            @Override
            public void onThrowable(@NotNull Throwable error) {
                //error.printStackTrace();
                NotifyUtil.showErrorNotification(project, PluginConstant.NOTIFICATION_ID_IMPORT_EXPORT, message("notify.import.throwable.title"), message("notify.import.throwable.message"), error.getMessage());
            }
        });
    }

    public static void publishReadOnlyMode(Project project, boolean b) {
        AppSettingsState.getInstance().readOnlyMode = b;
        ApplicationManager.getApplication()
                .getMessageBus()
                .syncPublisher(AppSettingsChangedListener.TOPIC)
                .onSetReadOnlyMode(project, b);
    }

    /**
     * 解析JSON数据为Java数据
     *
     * @param project      当前工程
     * @param selectedFile 选择的文件
     * @return java数据
     */
    @Nullable
    private static LinkedHashMap<Notebook, LinkedHashMap<Chapter, List<Note>>> processJson(Project project, VirtualFile selectedFile) {
        LinkedHashMap<Notebook, LinkedHashMap<Chapter, List<Note>>> notebooksCollection = null;
        try {
            notebooksCollection = new ObjectMapper().readValue(new File(selectedFile.getPath()), new TypeReference<LinkedHashMap<Notebook, LinkedHashMap<Chapter, List<Note>>>>() {
            });
        } catch (JsonProcessingException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String msg = sw.toString();
            NotifyUtil.showImportErrorNotification(project, PluginConstant.NOTIFICATION_ID_IMPORT_EXPORT,
                    message("notify.import.jsonException.title"),
                    message("notify.import.jsonException.message"),
                    e.getMessage() + System.lineSeparator() + msg);
        } catch (IOException exception) {
            NotifyUtil.showErrorNotification(project, PluginConstant.NOTIFICATION_ID_IMPORT_EXPORT,
                    message("notify.import.jsonIOException.title"), message("notify.import.jsonIOException.message"), exception.getMessage());
        }
        return notebooksCollection;
    }

    /**
     * 当Notebook出现同名时, 根据选择进行分支处理
     *
     * @param indicator    指示器, 可以用于随时监测用户是否点击了取消.好立刻停止.也可以用于进度的更新
     * @param entry        数据
     * @param notebookInDb 在数据库里已经存在的Notebook
     * @param userChoose   用户选择的处理方式
     * @return boolean 主要用于for循环监测是否需要放弃本次循环,进入下次循环. 如果为true,那么for循环continue
     */
    private static boolean nameConflictHandler(ProgressIndicator indicator, Map.Entry<Notebook, LinkedHashMap<Chapter, List<Note>>> entry, Notebook notebookInDb, int userChoose) {
        indicator.checkCanceled();
        if (userChoose == CHOOSE_OVERWRITE) {
            //第一步,删除旧的
            NotebookServiceImpl.getInstance().delete(notebookInDb.getId());
            //第二步,插入新的
            addNotebookFromJson(indicator, entry);
        } else if (userChoose == CHOOSE_SKIP) {
            //跳过
            return true;
        } else if (userChoose == CHOOSE_UPDATE) {
            indicator.setText("Update " + notebookInDb.getTitle());
            updateNotesFromJson(indicator, notebookInDb, entry);
        } else if (userChoose == CHOOSE_RENAME) {
            Random random = new Random();
            String newTitle = notebookInDb.getTitle() + "_" + System.currentTimeMillis() + (random.nextInt(900) + 100);
            indicator.setText("Import " + newTitle);
            entry.getKey().setTitle(newTitle);
            addNotebookFromJson(indicator, entry);
        }
        return false;
    }

    /**
     * 当数据库里不存在 同名的Notebook时 ,直接插入数据,无需提示用户选择
     *
     * @param indicator 指示器
     * @param entry     数据
     */
    private static void addNotebookFromJson(ProgressIndicator indicator, Map.Entry<Notebook, LinkedHashMap<Chapter, List<Note>>> entry) {
        //如果不存在, 那么插入数据即可
        indicator.checkCanceled();
        Notebook notebookInDb = NotebookServiceImpl.getInstance().insert(entry.getKey());
        LinkedHashMap<Chapter, List<Note>> hashMap = entry.getValue();
        Set<Map.Entry<Chapter, List<Note>>> chapterListEntries = hashMap.entrySet();
        for (Map.Entry<Chapter, List<Note>> chapterListEntry : chapterListEntries) {
            indicator.checkCanceled();
            Chapter tempChapter = chapterListEntry.getKey();
            tempChapter.setNotebookId(notebookInDb.getId());
            Chapter chapter = ChapterServiceImpl.getInstance().insert(tempChapter);
            addNotesFromJson(indicator, notebookInDb, chapterListEntry, chapter);
        }
    }

    /**
     * 当数据库里存在同名的Notebook时. 用户选择了更新, 那么如果数据库里同名的章节存在同名的Note时,默认保留为修改时间最近的Note
     *
     * @param indicator    指示器
     * @param notebookInDb 数据库里的Notebook
     * @param entry        数据
     */
    private static void updateNotesFromJson(ProgressIndicator indicator, Notebook notebookInDb, Map.Entry<Notebook, LinkedHashMap<Chapter, List<Note>>> entry) {
        ChapterServiceImpl chapterService = ChapterServiceImpl.getInstance();
        NoteServiceImpl noteService = NoteServiceImpl.getInstance();
        LinkedHashMap<Chapter, List<Note>> hashMap = entry.getValue();
        Set<Map.Entry<Chapter, List<Note>>> chapterListEntries = hashMap.entrySet();
        for (Map.Entry<Chapter, List<Note>> chapterListEntry : chapterListEntries) {
            Chapter chapterInJson = chapterListEntry.getKey();
            Chapter chapterInDb = chapterService.findByTitle(chapterInJson.getTitle(), notebookInDb.getId());

            List<Note> updateNoteList = new ArrayList<>();

            //如果数据库不存在该章节, 那么全部Note都插入
            if (chapterInDb == null) {
                chapterInDb = chapterService.insert(chapterInJson);
                chapterInDb.setNotebookId(notebookInDb.getId());
                addNotesFromJson(indicator, notebookInDb, chapterListEntry, chapterInDb);
            } else {
                //如果数据库存在同名章节, 那么保存时间最新的
                List<Note> noteList = chapterListEntry.getValue();
                List<Note> insertNewNoteList = new ArrayList<>();
                for (Note note : noteList) {
                    indicator.checkCanceled();
                    Note noteInDb = noteService.findByTitle(note.getTitle(), chapterInDb.getId());
                    // 如果数据库里存在同名的, 那么判断时间,是否要更新
                    if (noteInDb != null) {
                        if (note.getUpdateTime() > noteInDb.getUpdateTime()){
                            // 不需要更新showOrder; notebookId; chapterId; id; title
                            noteInDb.setContent(note.getContent());
                            noteInDb.setSource(note.getSource());
                            noteInDb.setDescription(note.getDescription());
                            noteInDb.setType(note.getType());
                            noteInDb.setCreateTime(note.getCreateTime());
                            noteInDb.setUpdateTime(note.getUpdateTime());
                            updateNoteList.add(noteInDb);
                        }
                        //如果数据库里不存在同名的,那么插入
                    } else {
                        // 如果数据库不存在同名的. 或者json的Note时间比较新
                        note.setNotebookId(notebookInDb.getId());
                        note.setChapterId(chapterInDb.getId());
                        insertNewNoteList.add(note);
                    }
                }
                indicator.checkCanceled();
                //先更新
                Note[] updateNotes = new Note[updateNoteList.size()];
                noteService.update(updateNoteList.toArray(updateNotes));

                //在添加新的
                Note[] newNotes = new Note[insertNewNoteList.size()];
                noteService.insert(insertNewNoteList.toArray(newNotes));
            }
        }
    }

    /**
     * 当无需判断,全部插入Notes
     *
     * @param indicator        指示器
     * @param notebookInDb     数据库里的Notebook
     * @param chapterListEntry 章节包含的Note数据
     * @param chapterInDb      章节数据
     */
    private static void addNotesFromJson(ProgressIndicator indicator, Notebook notebookInDb, Map.Entry<Chapter, List<Note>> chapterListEntry, Chapter chapterInDb) {
        List<Note> noteList = chapterListEntry.getValue();
        Note[] notes = new Note[noteList.size()];
        for (Note note : noteList) {
            note.setNotebookId(notebookInDb.getId());
            note.setChapterId(chapterInDb.getId());
        }
        for (int j = 0; j < noteList.size(); j++) {
            Note note = noteList.get(j);
            note.setNotebookId(notebookInDb.getId());
            note.setChapterId(chapterInDb.getId());
            notes[j] = note;
        }
        indicator.checkCanceled();
        NoteServiceImpl.getInstance().insert(notes);
    }

    /**
     * 同名Notebook出现时,提示用户进行处理
     *
     * @param title Notebook的标题
     * @return 用户的选择
     */
    private static UserChoose nameConflictDialog(String title) {
        UserChoose userChoose = new UserChoose();
        Messages.showDialog(
                "<html><body>" +
                        message("notify.import.nameConflict.message1") + "<br>" +
                        "<b>" + message("notify.import.nameConflict.message2") + "</b>" + title + "<br/>" +
                        "</html></body>"
                , message("notify.import.nameConflict.title"),
                new String[]{
                        //0
                        message("notify.import.nameConflict.chooseOverwrite"),
                        //1
                        message("notify.import.nameConflict.chooseSkip"),
                        //2
                        message("notify.import.nameConflict.chooseUpdate"),
                        //3
                        message("notify.import.nameConflict.chooseAutoRename")
                }, 1, Messages.getWarningIcon(), new DialogWrapper.DoNotAskOption.Adapter() {

                    @Override
                    public void rememberChoice(boolean isSelected, int exitCode) {
                        userChoose.setDoNotAsk(isSelected);
                        userChoose.setExitCode(exitCode);
                    }

                    @Override
                    public @NotNull String getDoNotShowMessage() {
                        return message("notify.import.nameConflict.rememberChoose");
                    }
                }
        );
        return userChoose;
    }

    private static class UserChoose {
        private boolean doNotAsk;
        private int exitCode;

        public boolean isDoNotAsk() {
            return doNotAsk;
        }

        public void setDoNotAsk(boolean doNotAsk) {
            this.doNotAsk = doNotAsk;
        }

        public int getExitCode() {
            return exitCode;
        }

        public void setExitCode(int exitCode) {
            this.exitCode = exitCode;
        }

        public UserChoose() {
        }

        @Override
        public String toString() {
            return "Choose{" +
                    "doNotAsk=" + doNotAsk +
                    ", exitCode=" + exitCode +
                    '}';
        }
    }
}
