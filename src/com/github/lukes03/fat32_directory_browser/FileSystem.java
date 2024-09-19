package com.github.lukes03.fat32_directory_browser;

import com.github.lukes03.fat32_directory_browser.fat32.DirectoryTable;
import com.github.lukes03.fat32_directory_browser.fat32.DirectoryTableEntry;
import com.github.lukes03.fat32_directory_browser.masterbootrecord.PartitionTableEntry;
import com.github.lukes03.fat32_directory_browser.masterbootrecord.PartitionTableEntryBytes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/***
 * This is the 'root class' of the software which is meant to encapsulate the program and provide methods to extract
 * files.
 */
public class FileSystem {
    private RandomAccessFile diskImage;
    private ArrayList<PartitionTableEntry> partitions = new ArrayList<>();
    private int blockSize = 512;
    private int partitionTableAddress = 0x1BE;
    private PartitionTableEntry currentPartition;

    public FileSystem(String diskImageName) throws FileNotFoundException {
        diskImage = new RandomAccessFile(diskImageName, "r");
        try {
            initialisePartitions();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //
    //
    // PRIVATE METHODS
    //
    //

    /**
     * This private method iterates through the partition table at the address specified at <code>partitionTableAddress</code>
     * to initialise a list of partitions.
     * @throws IOException
     */
    private void initialisePartitions() throws IOException {
        ArrayList<PartitionTableEntryBytes> partTableBytes = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            diskImage.seek(partitionTableAddress + (i * 16));
            byte[] byteBuffer = new byte[16];
            diskImage.read(byteBuffer, 0, 16);
            partTableBytes.add(new PartitionTableEntryBytes(byteBuffer));
        }
        for(PartitionTableEntryBytes b : partTableBytes) {
            partitions.add(new PartitionTableEntry(b));
        }
        return;
    }

    //
    //
    // GETTERS AND SETTERS
    //
    //
    //

    /**
     * Sets the currently active partition in the file-system.
     * @param index The index of the partition you wish to set as the current partition, where the index number
     *              corresponds to the index of the partition in the arraylist returned by the method
     *              <code>getPartitions()</code>.
     * @return The partition that has been set.
     * @throws ArrayIndexOutOfBoundsException Thrown if the index value is larger than the amount of partitions within
     *         the file-system, or if the index value is less than 0.
     */
    public PartitionTableEntry setCurrentPartitionIndex(int index) throws ArrayIndexOutOfBoundsException {
        if(index >= partitions.size() || index < 0) throw new ArrayIndexOutOfBoundsException("index parameter is an invalid value.");
        currentPartition = partitions.get(index);
        return currentPartition;
    }

    /**
     * Returns the currently active partition.
     * @return Currently active partition
     */
    public PartitionTableEntry getCurrentPartition() {
        return currentPartition;
    }

    /**
     * Returns an arraylist of partitions within the file-system.
     * @return Arraylist of partitions
     */
    public ArrayList<PartitionTableEntry> getPartitions() {
        return partitions;
    }

    /**
     * Returns the block-size of the file system in bytes.
     * @return Block-size in bytes.
     */
    public int getBlockSize() {
        return blockSize;
    }

    /**
     * Sets the block-size of the file system in bytes.
     * @param blockSize The block-size (in bytes) you wish to use.
     */
    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public byte[] getFileBytes(DirectoryTableEntry dirTableEntry) {
        return null;
    }

    public DirectoryTable getDirectory(DirectoryTableEntry dirTableEntry) {
        return null;
    }

    /**
     * @return A DirectoryTable instance representing the 'root' directory of a class.
     */
    public DirectoryTable getRootDirectory() {
        //TODO: Create a class to deal with the EBR.
        long partitionStartBytes = blockSize * currentPartition.getStartLBA();
        //This variable points to the bytes where the EBR stores the LBA address of the root directory. This is stored
        //at an offset of 0x02C bytes from the start of the EBR.
        long rootDirectoryLbaPointerBytes = partitionStartBytes + 0x02C;
        byte[] rootDirectoryLbaBuffer;
        return null;
    }
}
