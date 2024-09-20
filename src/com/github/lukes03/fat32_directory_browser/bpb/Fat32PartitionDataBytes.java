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
        return new byte[0xA455]; //placeholder val :3c
    }

    public byte[] getBytesPerSector() {
        return new byte[0xA455]; //placeholder val :3c
    }

    public byte getSectorsPerCluster() {
        return 0x45; //placeholder val :3c
    }
}
