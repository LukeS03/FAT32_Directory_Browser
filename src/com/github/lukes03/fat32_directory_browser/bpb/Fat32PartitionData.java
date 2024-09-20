package com.github.lukes03.fat32_directory_browser.bpb;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * This class encapsulates both the BIOS Parameter Block and the Extended Boot Record.
 * TODO: Comments!!!
 */
public class Fat32PartitionData {
    Fat32PartitionDataBytes bytes; //making pointless classes - they should hire me to write the java standard library!!!

    public Fat32PartitionData(Fat32PartitionDataBytes bytes) {
        this.bytes = bytes;
    }

    public String getVolumeLabel() {
        return new String(bytes.getVolumeLabel(), StandardCharsets.UTF_8);
    }

    public long getRootDirectoryCluster() {
        byte[] buffer = bytes.getRootDirectorySector();
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(buffer);
        return byteBuffer.getLong(0);
    }

    public int getBytesPerSector() {
        byte[] buffer = bytes.getBytesPerSector();
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(buffer);
        return byteBuffer.getInt(0);
    }

    public int getSectorsPerCluster() {
        return bytes.getSectorsPerCluster();
    }

    public int getReservedSectors() {
        byte[] buffer = bytes.getReservedSectors();
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(buffer);
        return byteBuffer.getInt(0);
    }

    public long getSectorsPerFAT() {
        byte[] buffer = bytes.getSectorsPerFAT();
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(buffer);
        return byteBuffer.getLong(0);
    }

    public int getNumFatTables() {
        return bytes.getNumFatTables();
    }

    public long getFirstDataSector() {
        //reserved sector count + (table count * fat size);
        long numReservedSectors = getReservedSectors() + (getNumFatTables() * getSectorsPerFAT());
        return numReservedSectors;
    }
}
