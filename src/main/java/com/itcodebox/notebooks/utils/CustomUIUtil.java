package com.itcodebox.notebooks.utils;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.util.ui.JBFont;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsState;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author LeeWyatt
 */
public class CustomUIUtil {
    public static JBFont getMyDefaultFont() {
        return JBFont.create(new Font(Font.MONOSPACED, Font.PLAIN, 18));
    }

    public static String getImgScr(String path) {
        URL resource = CustomUIUtil.class.getResource(path);
        if (resource == null) {
            return "";
        }
        String imgSrc = "";
        try {
            imgSrc = resource.toURI().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return imgSrc;
    }

    public static String getComboBoxText(ComboBox<String> comboBox) {
        ComboBoxEditor editor = comboBox.getEditor();
        Component editorComponent = editor != null ? editor.getEditorComponent() : null;
        String value = null;
        if (editorComponent instanceof JTextComponent) {
            JTextComponent component = (JTextComponent) editorComponent;
            value = component.getText();
        }
        return value;
    }

    public static Icon scaleImageIcon(@Nullable ImageIcon icon, int maxSize) {
        if (icon == null) {
            return null;
        }
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();
        //-1,-1当根据路径没有获取到图片时
        if (width == -1 || height == -1) {
            return null;
        }
        if (width > maxSize || height > maxSize) {
            if (width >= height) {
                double proportion= maxSize*1.0/width;
                width = maxSize;
                height = (int) (height*proportion);
                //win下 负数就会被替换成相应比列的数值; Mac下就会报错
            } else {
                double proportion= maxSize*1.0/height;
                height = maxSize;
                width = (int) (width*proportion);
            }
        }
        icon.setImage(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        return icon;
    }

    /**
     * Long类型->时间格式 yyyy/MM/dd HH:mm
     */
    public static String convertTimeToString(Long time){
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }


    /**
     * 从系统剪贴板中检索图像。
     *
     * @return 剪贴板中的图像，如果未找到图像则为 null
     */
    public static Image readFromClipboard() {
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        try {
            if (t != null && t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                return (Image) t.getTransferData(DataFlavor.imageFlavor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 将图像放在系统剪贴板上。
     *
     * @param image - 要添加到系统剪贴板的图像
     */
    public static void writeToClipboard(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("Exception:Image can't be null.");
        }
        ImageTransferable transferable = new ImageTransferable(image);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
    }

    private static class ImageTransferable implements Transferable {
        private final Image image;

        public ImageTransferable(Image image) {
            this.image = image;
        }

        @Override
        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            if (isDataFlavorSupported(flavor)) {
                return image;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor == DataFlavor.imageFlavor;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }
    }

    /**
     * 根据路径,获取图形的length
     * 如果图片不存在或者路径不符合或者不是文件或者不是图片文件,那么返回-1
     * 返回图片大小
     */
    public static long getImageLength(String path) {
        if (path == null || path.trim().isEmpty() || !path.contains(".")) {
            return -1;
        }
        //获取后缀名
        String extension = StringUtil.getExtension(path);
        //如果不是指定的后缀名, 那么返回false
        if (!PluginConstant.IMG_EXTENSION_LIST.contains(extension)) {
            return -1;
        }
        //如果后缀名也有了,那么就判断文件是否存在(以及文件大小)
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            return file.length();
        } else {
            return -1;
        }
    }


    public static String convertToThumbName(String imageName) {
       return new StringBuilder(imageName).insert(imageName.lastIndexOf("."),PluginConstant.ThumbExtension).toString();
    }

    public static File getThumbFile(String imageName) {
        String other = convertToThumbName(imageName);
        File file = PluginConstant.IMAGE_DIRECTORY_PATH.resolve(other).toFile();
        if (file.exists()) {
            return file;
        }else{
            file = PluginConstant.IMAGE_DIRECTORY_PATH.resolve(imageName).toFile();
            return file;
        }
    }

    /**
     * 根据图片和图片的最大尺寸来创建缩略图, 如果比例为1:1, 那么不用创建
     */
    public static void writeThumbImageToFile(File imageFile, File thumbFile) throws IOException {
        BufferedImage originImage = ImageIO.read(imageFile);
        int width = originImage.getWidth();
        int height = originImage.getHeight();
        double scale;
        int thumbMaxSize = AppSettingsState.getInstance().thumbMaxSize;
        if (width > thumbMaxSize || height > thumbMaxSize) {
            scale = thumbMaxSize * 1.0 / Math.max(width, height);
        }else{
            return;
        }
        //缩放后的size
        width = (int) (scale * originImage.getWidth());
        height = (int) (scale * originImage.getHeight());
        Image scaledImage = originImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        writeImageToFile(scaledImage, new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB), thumbFile, StringUtil.getExtension(imageFile.getName()));
    }

    public static void writeImageToFile(Image originImage, BufferedImage destImage, File destFile, String extension) throws IOException {
        Graphics2D graphics = destImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //不同插值效果. graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.drawImage(originImage, 0, 0, null);
        graphics.dispose();
        OutputStream out = new FileOutputStream(destFile);
        ImageIO.write(destImage, extension, destFile);
        out.close();
    }

}
