package com.example.filencrypt.models;

import android.content.Context;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface FilencryptFile extends Comparable<FilencryptFile> {
    List<FilencryptFile> getFiles();

    FilencryptFile getParent();

    String getName();

    String getPath();

    Uri getUri(Context context);

    boolean isUpFromRoot();

    boolean isRoot();

    boolean isDirectory();

    boolean isEncrypted();

    boolean exists();

    void delete();

    void delete(boolean ignoreError);

    FilencryptFile asEncrypted(FilencryptFile targetDirectory);

    FilencryptFile asPlain();

    OutputStream getOutputStream() throws FileNotFoundException;

    InputStream getInputStream() throws FileNotFoundException;

    long getSize();
}
