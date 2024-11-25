package com.github.lukes03.fat32_directory_browser.gui_interface.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Defines the list of files to be extracted from a file.
 */
public class ExtractionListModel {

    /**
     * Defines what files and directories are to be extracted from a file-system.
     */
    private ObservableSet<FileModel> extractionList;

    public ExtractionListModel() {
        extractionList = FXCollections.emptyObservableSet();
    }

    /**
     * Add a FileModel item to the extraction list.
     */
    public void addFile(FileModel f) {
        extractionList.add(f);
    }

    public void removeFile(FileModel f) {
        extractionList.remove(f);
    }

    public ObservableSet<FileModel> getExtractionListProperty() {return extractionList;}


}
