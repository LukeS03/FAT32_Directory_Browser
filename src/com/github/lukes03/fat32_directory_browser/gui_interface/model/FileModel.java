package com.github.lukes03.fat32_directory_browser.gui_interface.model;

import com.github.lukes03.fat32_directory_browser.libfat32.fat32.DirectoryTableEntry;
import com.github.lukes03.fat32_directory_browser.libfat32.fat32.FileAttribute;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FileModel {
    //TODO:
    // Could probably do with some kinda exception handling, detecting if the partition number has changed.

    /* === Get File Record Data === */
    private DirectoryTableEntry fileRecord;
    public DirectoryTableEntry getFileRecord() {return fileRecord;}

    private Fat32Model          fileSystemModel;

    /* === FILE NAME RELATED PROPERTIES. === */
    private StringProperty fileName; // LongFileName if exists, ShortFileName if it doesn't.
    public String         getFileName() {return fileName.get();}

    private StringProperty longFileName;
    public String          getLongFileName() {return longFileName.get();}

    private StringProperty shortFileName;
    public String          getShortFileName() {return shortFileName.get();}

    private StringProperty extension;
    public  String         getExtension() {
        if(this.fileAttribute.get() == FileAttribute.DIRECTORY) return "\uD83D\uDDC0";
        return extension.get();
    }

    /* === FILE ATTRIBUTE RELATED PROPERTIES === */
    private BooleanProperty isDirectory;
    public  Boolean         getIsDirectory() {return isDirectory.get();}

    private ObjectProperty<FileAttribute> fileAttribute;
    private StringProperty fileAttributeString;

    /* === DATA STORAGE RELATED PROPERTIES === */
    private LongProperty cluster;
    private LongProperty fileSizeBytes;
    public  Long         getFileSizeBytes() {return fileSizeBytes.get();}

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

    /* === EXTRACTION LIST MEMBERSHIP === */
    private BooleanProperty extractItem;
    public BooleanProperty extractItemProperty() {return extractItem;}
    public Boolean         isExtractItem() {return extractItem.get();}
    public void            setExtractItem(Boolean extractItem) {this.extractItem.set(extractItem);}

    // This parameter will be used to keep track of the path of the file.
    // ToDo
    private ArrayList<FileModel> filePath;
    public ArrayList<FileModel> getParents() {return filePath;}



    public FileModel(DirectoryTableEntry fileRecord, Fat32Model model, FileModel parent) {
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

        extractItem = new SimpleBooleanProperty(false);


        if(parent == null) this.filePath = new ArrayList<>();
        else {
            filePath = new ArrayList<>(parent.getParents());
            filePath.add(parent);
        }

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

    /**
     * @return True if file is not a "." or ".." file, denoting a link to the current or previous directory respectively.
     */
    public boolean isValidDirectoryChild() {
        switch(getShortFileName()) {
            case ".       ", "..      ":
                return false;
            default:
                return true;
        }
    }
}
