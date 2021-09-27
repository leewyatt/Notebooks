package com.itcodebox.notebooks.utils;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.entity.ImageRecord;
import org.apache.commons.io.file.PathUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class CustomFileUtil {

    /**
     * 统计(缩略图)文件夹的大小 (不包含子目录, 因为缓存图片,都在同一目录下)
     */
    public static long sizeOfDirectory(File directory) {
        if (directory == null || !directory.exists()) {
            return 0L;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return 0L;
        } else {
            long size = 0L;
            for (File file : files) {
                size += file.length();
                if (size < 0L) {
                    break;
                }
            }
            return size;
        }
    }

    /**
     * 删除(缩略图)文件夹下的全部文件
     */
    public static void deleteFilesUnderDirectory(File directory) {
        if (directory == null || !directory.exists()) {
            return;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    public static Path choosePath(Project project, String defaultDirName) {
        FileChooserDialog fileChooser = FileChooserFactory.getInstance().createFileChooser(
                new FileChooserDescriptor(false, true, false, false, false, false),
                project, null);
        VirtualFile[] choose = fileChooser.choose(project, VirtualFile.EMPTY_ARRAY);
        if (choose.length != 0) {
            Path parentPath = choose[0].toNioPath();

            boolean canWrite = parentPath.toFile().canWrite();
            if (!canWrite) {
                Messages.showInfoMessage(message("fileUtil.writeError.message"), message("fileUtil.writeError.title"));
                return null;
            }
            String dirName = inputDirectory(parentPath, defaultDirName);
            if (dirName == null) {
                return null;
            }
            try {
                return Files.createDirectories(parentPath.resolve(dirName));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    public static String inputDirectory(Path parentPath, String defaultDirName) {
        String dirName = Messages.showInputDialog(message("fileUtil.exportDir.msg"), message("fileUtil.exportDir.title"), null, defaultDirName, new InputValidatorEx() {
            @Override
            public @NlsContexts.DetailedDescription @Nullable String getErrorText(@NonNls String inputString) {
                String str = inputString.trim();
                if (str.isEmpty()) {
                    return message("fileUtil.exportDir.error.empty");
                }

                File file = null;
                try {
                    file = parentPath.resolve(str).toFile();
                } catch (InvalidPathException exception) {
                    return message("fileUtil.exportDir.error.path");
                }
                if (file.exists()) {
                    return message("fileUtil.exportDir.error.exists");
                }

                return null;
            }

            @Override
            public boolean checkInput(@NlsSafe String inputString) {
                String str = inputString.trim();
                if (str.isEmpty()) {
                    return false;
                }
                File file = null;
                try {
                    file = parentPath.resolve(str).toFile();
                } catch (InvalidPathException exception) {
                    return false;
                }
                return !file.exists();
            }

            @Override
            public boolean canClose(@NlsSafe String inputString) {
                return checkInput(inputString);
            }

        });
        return dirName;
    }

    /**
     * 复制(图片)文件夹
     */
    public static void copyDirectory(File originDir, File destDir) throws IOException {
        File[] files = originDir.listFiles();
        if (files == null) {
            return;
        }
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        for (File file : files) {
            FileUtil.copy(file, destDir.toPath().resolve(file.getName()).toFile());
        }
    }

    /**
     * 注意 1.  List imageRecords 这里每一条记录都是JSON ,还需要转一次才能得到路径
     * 注意 2.  导出图片时,无需导出缩略图, 因为这里是Markdown需要原图,无需缩略图
     */
    public static void exportImagesToDirectory(List<String> imageRecords, File destDir) throws IOException {
        for (String imgRecordStr : imageRecords) {
            if (imgRecordStr == null || imgRecordStr.trim().isEmpty()) {
                continue;
            }
            List<ImageRecord> recordList = ImageRecordUtil.convertToList(imgRecordStr);
            for (ImageRecord imageRecord : recordList) {
                try {
                    File fromFile = PluginConstant.IMAGE_DIRECTORY_PATH.resolve(imageRecord.getImagePath()).toFile();
                    File toFile = destDir.toPath().resolve(imageRecord.getImagePath()).toFile();
                    if (!fromFile.exists() || toFile.exists()) {
                        continue;
                    }
                    FileUtil.copy(fromFile, toFile);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除图片时,记得删除缩略图
     *
     * @param imageRecords
     */
    public static void deleteImages(List<String> imageRecords) {
        for (String imgRecordStr : imageRecords) {
            if (imgRecordStr == null || imgRecordStr.trim().isEmpty()) {
                continue;
            }
            List<ImageRecord> recordList = ImageRecordUtil.convertToList(imgRecordStr);
            for (ImageRecord imageRecord : recordList) {
                try {
                    deleteImagesAndThumb(imageRecord.getImagePath());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public static void deleteImagesAndThumb(String imageName) throws IOException {
        PathUtils.deleteFile(PluginConstant.IMAGE_DIRECTORY_PATH.resolve(imageName));
        PathUtils.deleteFile(PluginConstant.IMAGE_DIRECTORY_PATH.resolve(CustomUIUtil.convertToThumbName(imageName)));
    }
}
