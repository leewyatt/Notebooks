package com.itcodebox.notebooks.ui.dialog;

import java.util.Arrays;

/**
 * @author LeeWyatt
 */
public class Tip {
    private String title;
    private String[] infos;
    private String imgPath;

    public Tip() {
    }

    public Tip(String title, String[] infos, String imgPath) {
        this.title = title;
        this.infos = infos;
        this.imgPath = imgPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getInfos() {
        return infos;
    }

    public void setInfos(String[] infos) {
        this.infos = infos;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public String toString() {
        return "Tip{" +
                "title='" + title + '\'' +
                ", infos=" + Arrays.toString(infos) +
                ", imgPath='" + imgPath + '\'' +
                '}';
    }
}
