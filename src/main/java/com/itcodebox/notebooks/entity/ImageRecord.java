package com.itcodebox.notebooks.entity;

import java.util.Objects;

/**
 * @author LeeWyatt
 */
public class ImageRecord {
    private String imageTitle;
    private String imageDesc;
    private String imagePath;

    public ImageRecord() {
    }

    public ImageRecord(String imageTitle, String imageDesc, String imagePath) {
        this.imageTitle = imageTitle;
        this.imageDesc = imageDesc;
        this.imagePath = imagePath;
    }

    public String getImageDesc() {
        return imageDesc;
    }

    public void setImageDesc(String imageDesc) {
        this.imageDesc = imageDesc;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImageRecord that = (ImageRecord) o;
        return Objects.equals(imageTitle, that.imageTitle) && Objects.equals(imageDesc, that.imageDesc) && Objects.equals(imagePath, that.imagePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageTitle, imageDesc, imagePath);
    }

    @Override
    public String toString() {
        return "ImageRecord{" +
                "title='" + imageTitle + '\'' +
                ", imageDesc='" + imageDesc + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
