package com.example.mds_user.demovideo.filelist;

import android.net.Uri;

import java.io.File;

/**
 * Created by mds_user on 2018/7/20.
 */

public class FileData {
    File file = null;
    String name = "";
    String size = "";
    String time = "";
    String path = "";

    public FileData(File file,String name, String size, String time, String path) {
        this.name = name;
        this.size = size;
        this.time = time;
        this.path = path;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
