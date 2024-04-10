package com.example.filencrypt.models;

import java.util.List;

public interface OnChangeListener {
    void onSelectionChanged(List<FilencryptFile> selectedFiles);
}
