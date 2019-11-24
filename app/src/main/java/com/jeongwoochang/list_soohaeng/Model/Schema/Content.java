package com.jeongwoochang.list_soohaeng.Model.Schema;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class Content implements Serializable {
    private String fileName;

    private String extension;

    private byte[] content;

    public Content() {
    }

    public Content(String fileName, String extension, byte[] content) {
        this.fileName = fileName;
        this.extension = extension;
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getFullFileName(){
        return getFileName()+"."+extension;
    }

    @NonNull
    @Override
    public String toString() {
        return "{"+getFileName()+"."+extension+"}";
    }
}
