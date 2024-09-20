package com.github.lukes03.fat32_directory_browser;

import com.github.lukes03.fat32_directory_browser.bpb.Fat32PartitionData;
import com.github.lukes03.fat32_directory_browser.bpb.Fat32PartitionDataBytes;
import com.github.lukes03.fat32_directory_browser.fat32.DirectoryTable;
import com.github.lukes03.fat32_directory_browser.fat32.DirectoryTableEntry;
import com.github.lukes03.fat32_directory_browser.masterbootrecord.PartitionTableEntry;
import com.github.lukes03.fat32_directory_browser.masterbootrecord.PartitionTableEntryBytes;

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
    private Fat32PartitionData currentPartitionData;

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

        //init bpb and ebr
        byte[] partitionDataBytes = new byte[512];
        try {
            diskImage.seek(getPartitionStartByteOffset());
            diskImage.read(partitionDataBytes, 0, 512);
            currentPartitionData = new Fat32PartitionData(new Fat32PartitionDataBytes(partitionDataBytes));
            return currentPartition;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        return initialiseDirectoryFromClusterNumber(currentPartitionData.getRootDirectoryCluster());
    }

    /**
     * Return the byte offset of the start of a partition from the first byte of a file system image.
     * @return
     */
    public long getPartitionStartByteOffset() {
        return blockSize * currentPartition.getStartLBA();
    }

    /**
     * Returns a directory table from a file entry within another directory table.
     * @param dirTableEntry The directory table entry of the directory you want to initialise.
     * @return
     */
    public DirectoryTable getDirectory(DirectoryTableEntry dirTableEntry) throws IOException {
        return initialiseDirectoryFromClusterNumber(dirTableEntry.getCluster());
    }

    /**
     * Initialise a directory table using the sector address of the directory.
     * @param clusterNumber
     * @return
     */
    private DirectoryTable initialiseDirectoryFromClusterNumber(long clusterNumber) throws IOException {

        /* calculate byte offset of file. */
        long partitionByteOffset = getPartitionStartByteOffset(); // byte offset of partition.
        long reservedSectorsByteOffset = currentPartitionData.getFirstDataSector() * currentPartitionData.getBytesPerSector();
        long clusterByteOffset = (clusterNumber-2) * ((long) currentPartitionData.getBytesPerSector() * currentPartitionData.getSectorsPerCluster());
        long seekOffset = partitionByteOffset + reservedSectorsByteOffset + clusterByteOffset;


        ArrayList<byte[]> byteChunkBuffer = new ArrayList<>(); // buffer for each of the 32-byte "chunks" read.

        // Read the directory table into a byte arraylist until it reaches a 32 byte "chunk" whose first byte is equal
        // to zero, indicating the end of the directory.
        // The last entry will always be an invalid entry so we'll just quickly shave that off afterwards.
        byte[] dirTableBuffer = new byte[32];
        do {
            diskImage.seek(seekOffset);
            diskImage.read(dirTableBuffer, 0, 32);
            byteChunkBuffer.add(dirTableBuffer);
            seekOffset+=32;
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

    private void readPartition() {
        /*
        initialise partition blah blah blah
         */
    }
}
