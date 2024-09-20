package com.github.lukes03.fat32_directory_browser.bpb;

/**
 * This class encapsulates both the BIOS Parameter Block and the Extended Boot Record.
 */
public class Fat32PartitionData {
    Fat32PartitionDataBytes bytes; //making pointless classes - they should hire me to write the java standard library!!!

    public Fat32PartitionData(Fat32PartitionDataBytes bytes) {
        this.bytes = bytes;
    }

    public long getRootDirectorySector() {
        return 0;
    }

    public int getBytesPerSector() {
        return 0;
    }

    public int getSectorsPerCluster() {
        return 0;
    }
}
