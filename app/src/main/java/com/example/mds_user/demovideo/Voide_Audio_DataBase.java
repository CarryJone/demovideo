package com.example.mds_user.demovideo;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by mds_user on 2018/8/17.
 */

public class Voide_Audio_DataBase {
    private File file = null;
    private File original_file = null;
    private String filename = "";
    private   Map<String,String> data = null;
    private String status = "未上傳";
    private String creatid = null;
    public Voide_Audio_DataBase(File file,   Map<String,String> data) {
        this.file = file;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public  Map<String,String> getData() {
        return data;
    }

    public void setData( Map<String,String> data) {
        this.data = data;
    }

    public File getOriginal_file() {
        return original_file;
    }

    public void setOriginal_file(File original_file) {
        this.original_file = original_file;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getCreatid() {
        return creatid;
    }

    public void setCreatid(String creatid) {
        this.creatid = creatid;
    }
}
