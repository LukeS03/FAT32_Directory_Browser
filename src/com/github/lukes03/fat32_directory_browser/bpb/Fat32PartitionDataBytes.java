package com.github.lukes03.fat32_directory_browser.bpb;

/**
 * Class to encapsulate the raw bytes of a FAT 32 partition's BPB and EBR.
 */
public class Fat32PartitionDataBytes {
    private byte[] bytes;

    public Fat32PartitionDataBytes(byte[] bytes) {
        if(bytes.length != 0x200) throw new IllegalArgumentException("BPB and EBR must be 512 bytes.");
        else this.bytes = bytes;
    }

    /**
     * Returns the raw bytes that this instance encapsulates.
     * @return
     */
    public byte[] getBytes() {
        return bytes;
    }

    public byte[] getRootDirectorySector() {
        byte[] buffer = new byte[4];
        System.arraycopy(bytes, 0x02C, buffer, 0, 4);
        return buffer;
    }

    public byte[] getVolumeLabel() {
        byte[] buffer = new byte[11];
        System.arraycopy(bytes, 0x047, buffer, 0, 11);
        return buffer;
    }

    public byte getSectorsPerCluster() {
        return bytes[0x0D];
    }

    public byte[] getBytesPerSector() {
        byte[] buffer = new byte[2];
        System.arraycopy(bytes, 0x0B, buffer, 0, 2);
        return buffer;
    }

    public byte[] getReservedSectors() {
        byte[] buffer = new byte[2];
        System.arraycopy(bytes, 0x0E, buffer, 0, 2);
        return buffer;
    }

    public byte[] getSectorsPerFAT() {
        byte[] buffer = new byte[4];
        System.arraycopy(bytes, 0x024, buffer, 0, 4);
        return buffer;
    }

    public byte getNumFatTables() {
        return bytes[0x10];
    }


}
