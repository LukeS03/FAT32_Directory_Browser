package com.github.lukes03.fat32_directory_browser.gui_interface.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Defines the list of files to be extracted from a file.
 */
public class ExtractionListModel {

    /**
     * Defines what files and directories are to be extracted from a file-system.
     */
    private ObservableSet<FileModel> extractionList;

    public ExtractionListModel() {
        extractionList = FXCollections.observableSet(new HashSet<>());
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

    /**
     * Cuts the need by nesting by combining addFile and removeFile into one method.
     * @param f
     * @param extractItem true = add file to extraction list, false = remove file from extraction list.
     */
    public void enableFileExtraction(FileModel f, boolean extractItem) {
        if(extractItem) addFile(f);
        else removeFile(f);
    }

    public ObservableSet<FileModel> getExtractionListProperty() {return extractionList;}

    public void resetExtractionList() {extractionList.clear();}

}
