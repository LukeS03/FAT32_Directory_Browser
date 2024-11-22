package com.github.lukes03.fat32_directory_browser.gui_interface.model;

import com.github.lukes03.fat32_directory_browser.libfat32.FileSystem;
import com.github.lukes03.fat32_directory_browser.libfat32.fat32.DirectoryTable;
import com.github.lukes03.fat32_directory_browser.libfat32.fat32.DirectoryTableEntry;
import com.github.lukes03.fat32_directory_browser.libfat32.masterbootrecord.PartitionTableEntry;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Fat32Model {
    /* RANT
     *
     * I don't know if it's the fact that I haven't coded much Java and don't know too much about the Java compiler
     * or virtual machine, but OMG GETTERS AND SETTERS ARE SO GOD DAMN ANNOYING. I don't get it. Why do I want to
     * litter my code with a million different damned getters and setters? This is silly. Why can't we have
     * properties like C#? If getters and setters are oh-so-damned important for object orientation why can't they
     * be implied in the same way that constructors are?
     *
     * TLDR: java team pls copy c# thx babe
     *
     * /RANT
     */


    /* These are mostly internal variables but I made them properties just in case they need to be listened to. */
    private ObjectProperty<DirectoryTable> root;

    private StringProperty filePath;
    public  StringProperty getFilePathProperty() {return filePath;}

    /*================================================================================================================*/
    private IntegerProperty partitionNumber;    // Denotes which partition is currently selected so that view can be updated when a new partition is loaded.
    public IntegerProperty  getPartitionNumberProperty() {return partitionNumber;}

    private ObjectProperty<FileSystem> fileSystem; // Denotes that a new file has been loaded.

    private BooleanProperty[] validPartitions; // Used for "Partitions" menu radio buttons to tell which partitions are valid.
    public BooleanProperty[]  getValidPartitionsProperty() {return validPartitions; /*I'm pretty sure this is a bad idea. Can't you just directly change the value using the reference returned? Oh well, OO is dumb.*/}

    /* Note to Self...
     * Things app needs to do...
     * 1. Select a partition
     * 2. View root directory
     * 3. Explore new directories
     * 4. Mark files and directories for extraction
     *
     * All of these should probably be their own methods for the GUI app
     * which draw from the underlying Fat32 library.
     */

    public Fat32Model() {
        fileSystem = new SimpleObjectProperty<>(null);
        root = new SimpleObjectProperty<>(null);
        partitionNumber = new SimpleIntegerProperty(-1);
        filePath = new SimpleStringProperty(null);
        validPartitions = new BooleanProperty[]{new SimpleBooleanProperty(false),
                new SimpleBooleanProperty(false),
                new SimpleBooleanProperty(false),
                new SimpleBooleanProperty(false)};
    }

    public void openNewFile(String filePath) throws FileNotFoundException {
        this.filePath.set(filePath);
        initNewFile();
    }

    /**
     * Runs when a new FAT32 binary image is loaded. Initialises the file system.
     */
    private void initNewFile() throws FileNotFoundException {
        /*
         * ToDo
         *  1. Check if partitions are valid, mark invalid partitions as disabled
         *  2. Init a new fileSystem using the filepath.
         */

        fileSystem.setValue(new FileSystem(filePath.get()));
        ArrayList<PartitionTableEntry> partitionsData = fileSystem.getValue().getPartitions();
        for(int i = 0; i < 4; i++) {
            validPartitions[i].setValue(partitionsData.get(i).isValidPartition());
        }
        partitionNumber.set(-1);
    }

    public void setNewPartition(int newPartition) {
        fileSystem.get().setCurrentPartitionIndex(newPartition);
        partitionNumber.set(newPartition);
    }

    public ObservableList<FileModel> getRootDirectory() throws IOException {
        DirectoryTable directoryTable = fileSystem.get().getRootDirectory();
        return directoryTableToList(directoryTable);
    }

    /**
     * Get the children of a directory.
     */
    public ObservableList<FileModel> getDirectoryChildren(FileModel parent) throws IOException {
        DirectoryTable directoryTable = fileSystem.get().getDirectory(parent.getFileRecord());
        return directoryTableToList(directoryTable);
    }

    private ObservableList<FileModel> directoryTableToList(DirectoryTable directoryTable) {
        ArrayList<DirectoryTableEntry> directoryTableEntries = directoryTable.getEntries();

        ObservableList<FileModel> returnList = FXCollections.observableArrayList();

        for(DirectoryTableEntry e : directoryTableEntries) {
            returnList.add(new FileModel(e, this));
        }

        return returnList;

    }
}
