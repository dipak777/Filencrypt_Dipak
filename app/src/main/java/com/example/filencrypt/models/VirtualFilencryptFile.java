package com.example.filencrypt.models;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class VirtualFilencryptFile implements FilencryptFile {
    private String name;
    private int size;
    private InputStream inputStream;

    public VirtualFilencryptFile(String name, int size, InputStream inputStream) {
        this.name = name;
        this.size = size;
        this.inputStream = inputStream;
    }

    @Override
    public List<FilencryptFile> getFiles() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FilencryptFile getParent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uri getUri(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUpFromRoot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRoot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isEncrypted() {
        return false;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(boolean ignoreError) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FilencryptFile asEncrypted(FilencryptFile targetDirectory) {
        if (targetDirectory == null) throw new UnsupportedOperationException();
        String path = targetDirectory.getPath() + File.separator + this.getName() + ".pri";
        return new ConcreteFilencryptFile(path);
    }

    @Override
    public FilencryptFile asPlain() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream getOutputStream() throws FileNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getInputStream() throws FileNotFoundException {
        return this.inputStream;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public int compareTo(@NonNull FilencryptFile o) {
        throw new UnsupportedOperationException();
    }
}
