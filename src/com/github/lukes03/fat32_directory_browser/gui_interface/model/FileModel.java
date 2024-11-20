package com.github.lukes03.fat32_directory_browser.gui_interface.model;

import com.github.lukes03.fat32_directory_browser.libfat32.fat32.DirectoryTableEntry;
import com.github.lukes03.fat32_directory_browser.libfat32.fat32.FileAttribute;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FileModel {
    //TODO:
    // Could probably do with some kinda exception handling, detecting if the partition number has changed.

    /* === Get File Record Data === */
    private DirectoryTableEntry fileRecord;
    public DirectoryTableEntry getFileRecord() {return fileRecord;}

    private Fat32Model          fileSystemModel;

    /* === FILE NAME RELATED PROPERTIES. === */
    private StringProperty fileName; // LongFileName if exists, ShortFileName if it doesn't.
    public StringProperty fileNameProperty() {return fileName;}

    private StringProperty longFileName;
    private StringProperty shortFileName;
    private StringProperty extension;

    /* === FILE ATTRIBUTE RELATED PROPERTIES === */
    private BooleanProperty isDirectory;
    private ObjectProperty<FileAttribute> fileAttribute;
    private StringProperty fileAttributeString;

    /* === DATA STORAGE RELATED PROPERTIES === */
    private LongProperty cluster;
    private LongProperty fileSizeBytes;

    /* === DATE AND TIME RELATED PROPERTIES === */
    private ObjectProperty<LocalDateTime> dateTimeCreated;
    private StringProperty dateTimeCreatedString;
    private ObjectProperty<LocalDateTime> dateTimeModified;
    private StringProperty dateTimeModifiedString;
    private ObjectProperty<LocalDate>     dateAccessed;
    private StringProperty dateAccessedString;

    /* === DIRECTORY CONTENTS === */
    /**If isDirectory == true, contains list of files contained in the directory.*/
    private ObservableList<FileModel> children = null;



    public FileModel(DirectoryTableEntry fileRecord, Fat32Model model) {
        this.fileRecord      = fileRecord;
        this.fileSystemModel = model;

        fileName      = new SimpleStringProperty(fileRecord.getFileName());
        longFileName  = new SimpleStringProperty(fileRecord.getLongFileName());
        shortFileName = new SimpleStringProperty(fileRecord.getShortFileName());
        extension     = new SimpleStringProperty(fileRecord.getExtension());

        isDirectory = new SimpleBooleanProperty(fileRecord.getFileAttribute() == FileAttribute.DIRECTORY);
        fileAttribute = new SimpleObjectProperty<>(fileRecord.getFileAttribute());
        fileAttributeString = new SimpleStringProperty(fileRecord.getFileAttribute().attributeName);

        cluster = new SimpleLongProperty(fileRecord.getCluster());
        fileSizeBytes = new SimpleLongProperty(fileRecord.getFileSize());

        dateTimeCreated = new SimpleObjectProperty<>(fileRecord.getDateTimeCreated());
        dateTimeModified = new SimpleObjectProperty<>(fileRecord.getDateTimeModified());
        dateAccessed     = new SimpleObjectProperty<>(fileRecord.getDateAccessed());

    }

    /**
     * Returns an ObservableList of files contained within a directory. If used on a file that is not a directory, throws
     * a runtime exception.
     * @return
     */
    public ObservableList<FileModel> getChildDirectories() throws IOException {
        if(isDirectory.get() != true) throw new RuntimeException("FileModel instance is not a directory.");

        if(children == null) {
            children = fileSystemModel.getDirectoryChildren(this);
        }

        return children;
    }
}
