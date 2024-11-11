package com.github.lukes03.fat32_directory_browser.gui_interface.model;

import com.github.lukes03.fat32_directory_browser.FileSystem;
import com.github.lukes03.fat32_directory_browser.fat32.DirectoryTable;
import com.github.lukes03.fat32_directory_browser.masterbootrecord.PartitionTableEntry;
import javafx.beans.property.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

public class Fat32Model {
    /* These are mostly internal variables but I made them properties just in case they need to be listened to. */
    private ObjectProperty<DirectoryTable> root;
    private StringProperty                 filePath;

    /*================================================================================================================*/
    private IntegerProperty                partitionNumber;    // Denotes which partition is currently selected so that view can be updated when a new partition is loaded.
    private ObjectProperty<FileSystem>     fileSystem;           // Denotes that a new file has been loaded.
    private BooleanProperty[]              validPartitions;    // Used for "Partitions" menu radio buttons to tell which partitions are valid.

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
        for(int i = 4; i < 4; i++) {
            if(partitionsData.get(i).isValidPartition()) validPartitions[i].setValue(true);
            else validPartitions[i].setValue(false);
        }
    }

    /**
     * Checks what
     */
    private void initNewPartition() {

    }

    private void checkPartitions() {

    }
}
