package com.github.lukes03.fat32_directory_browser.masterbootrecord;

/***
 * See <a href="https://wiki.osdev.org/MBR_(x86)">osdev wiki: MBR (x86)</a>
 */
public class PartitionTableEntryBytes {
    private final byte[] bytes;

    public PartitionTableEntryBytes(byte[] bytes) {
        this.bytes = bytes;
        if(bytes.length != 16) throw new IllegalArgumentException("Partition table entry must be 16 bytes.");
    }

    public byte getDriveAttributes() {
        return bytes[0];
    }

    public byte[] getChsStartBytes() {
        byte[] chsBytes = new byte[3];
        System.arraycopy(bytes, 1, chsBytes, 0, 3);
        return chsBytes;
    }

    public byte getPartitionType() {
        return bytes[1];
    }

    public byte[] getChsLastBytes() {
        byte[] chsBytes = new byte[3];
        System.arraycopy(bytes, 5, chsBytes, 0, 3);
        return chsBytes;
    }

    public byte[] getStartLbaAddress() {
        byte[] lbaBytes = new byte[4];
        System.arraycopy(bytes, 8, lbaBytes, 0, 4);
        return lbaBytes;
    }

    public byte[] getNumSectors() {
        byte[] sectorBytes = new byte[4];
        System.arraycopy(bytes, 12, sectorBytes, 0, 4);
        return sectorBytes;
    }
}
