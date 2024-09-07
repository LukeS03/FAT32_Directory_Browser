package com.github.lukes03.fat32_directory_browser.fat32;

/**
 * A long file name.
 *
 * See <a href="https://wiki.osdev.org/FAT#Long_File_Names">OSDev Wiki</a>
 */
public class LongFileNameBytes extends DirectoryEntryBytes {

    public LongFileNameBytes(byte[] bytes) throws IllegalArgumentException {
        super(bytes);
    }

    /**
     * Get the index which specifies which part of the long file name this entry is.
     * @return
     */
    public byte entryIndex() {return bytes[0];}

    /**
     * Returns all of the bytes in the LFN entry used to store the characters for the LFN name.
     * @return
     */
    public byte[] getLongFileNameBytes() {
        byte[] lfnBuffer = new byte[26];
        int bytePtr = 0;
        for(int i = 1; i < 11; i++) {
            lfnBuffer[bytePtr] = bytes[i];
            bytePtr++;
        }
        for(int i = 14; i < 26; i++) {
            lfnBuffer[bytePtr] = bytes[i];
            bytePtr++;
        }
        for(int i = 28; i < 32; i++) {
            lfnBuffer[bytePtr] = bytes[i];
            bytePtr++;
        }
        return lfnBuffer;
    }

    /**
     * Get the checksum corresponding to this LFN entry's corresponding 8.3 Standard directory table entry.
     * @return
     */
    public byte getShortFileNameChecksum() {return bytes[13];}
}
