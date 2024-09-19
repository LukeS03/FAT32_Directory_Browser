package com.github.lukes03.fat32_directory_browser;

import com.github.lukes03.fat32_directory_browser.fat32.DirectoryTable;
import com.github.lukes03.fat32_directory_browser.fat32.DirectoryTableEntry;
import com.github.lukes03.fat32_directory_browser.masterbootrecord.PartitionTableEntry;
import com.github.lukes03.fat32_directory_browser.masterbootrecord.PartitionTableEntryBytes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

    /**
     * @return A DirectoryTable instance representing the 'root' directory of a class.
     */
    public DirectoryTable getRootDirectory() throws IOException {
        //TODO: Create a class to deal with the EBR.

        //This variable points to the bytes where the EBR stores the LBA address of the root directory. This is stored
        //at an offset of 0x02C bytes from the start of the EBR.
        long rootDirectoryLbaPointerBytes = getPartitionStartOffset(currentPartition) + 0x02C;
        diskImage.seek(rootDirectoryLbaPointerBytes);

        //Read LBA bytes into a buffer.
        byte[] rootDirectoryLbaBuffer = new byte[4];
        diskImage.read(rootDirectoryLbaBuffer, 0, 4);


        //Initialise a byte buffer to convert the extracted bytes into a long.
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(rootDirectoryLbaBuffer);

        for(int i = 0; i < 4; i++) byteBuffer.put((byte)0);

        long rootDirectoryLba = byteBuffer.getLong(0);

        DirectoryTable rootDirectory = initialiseDirectoryFromLba(rootDirectoryLba);

        return rootDirectory;
    }

    /**
     * Return the byte offset of a file.
     * @param file The DirectoryTableEntry of the file whose address you want.
     * @return The byte offset of a file from the start of it's partition.
     */
    public long getFileOffset(DirectoryTableEntry file) {
        return blockSize * file.getCluster();
    }

    /**
     * Return the byte offset of the start of a partition from the first byte of a file system image.
     * @param partition
     * @return
     */
    public long getPartitionStartOffset(PartitionTableEntry partition) {
        return blockSize * partition.getStartLBA();
    }

    /**
     * Returns a directory table from a file entry within another directory table.
     * @param dirTableEntry The directory table entry of the directory you want to initialise.
     * @return
     */
    public DirectoryTable getDirectory(DirectoryTableEntry dirTableEntry) throws IOException {
        return initialiseDirectoryFromLba((dirTableEntry.getCluster()));
    }

    /**
     * Initialise a directory table using the LBA address of the directory.
     * @param lbaAddress
     * @return
     */
    private DirectoryTable initialiseDirectoryFromLba(long lbaAddress) throws IOException {
        long byteOffset = lbaAddress * blockSize; // byte offset of directory.
        diskImage.seek(byteOffset);

        int tablePointer = 0; //amount of 32 byte chunks read, used to set offset for seek.

        ArrayList<byte[]> byteChunkBuffer = new ArrayList<>(); // buffer for each of the 32-byte "chunks" read.

        // Read the directory table into a byte arraylist until it reaches a 32 byte "chunk" whose first byte is equal
        // to zero, indicating the end of the directory.
        // The last entry will always be an invalid entry so we'll just quickly shave that off afterwards.
        byte[] dirTableBuffer = new byte[32];
        do {
            diskImage.read(dirTableBuffer, tablePointer * 32, 32);
            byteChunkBuffer.add(dirTableBuffer);
            tablePointer++;
        } while(dirTableBuffer[0] != 0);

        //Shave off the final invalid entry. That's better!
        byteChunkBuffer.remove(byteChunkBuffer.size()-1);

        //A byte array of the aforementioned bytes combined into a single array so that it can be passed to the constructor
        //for DirectoryTable.
        int byteArrayLen = byteChunkBuffer.size() * 32;
        byte[] dirTableBytes = new byte[byteArrayLen];

        int dirTableBytesIndex = 0;

        for(byte[] byteArray : byteChunkBuffer) {
            System.arraycopy(byteArray, 0, dirTableBytes, dirTableBytesIndex, 32);
            dirTableBytesIndex+=32;
        }

        return new DirectoryTable(dirTableBytes);
    }
}
