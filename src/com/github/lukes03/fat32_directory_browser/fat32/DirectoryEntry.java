package com.github.lukes03.fat32_directory_browser.fat32;

/**
 * Parent class for com.github.lukes03.fat32_directory_browser.fat32.DirectoryFileEntry and com.github.lukes03.fat32_directory_browser.fat32.LongFileName
 * <br>This just stores a 32 byte long array representing a single entry in a com.github.lukes03.fat32_directory_browser.fat32.DirectoryTable.
 *
 */
abstract class DirectoryEntry {
    byte[] bytes;
    byte[] byteBuffer;
    boolean isLfn;
    /**
     * Construct an entry from a directory entry table using an array of 32 bytes.
     * @param bytes A 32 byte-long array from which the directory entry table is to be constructed.
     * @throws IllegalArgumentException Exception thrown if the parameter 'bytes' is not a 32 byte long array.
     */
    public DirectoryEntry(byte[] bytes) throws IllegalArgumentException {
        if(bytes.length != 32) throw new IllegalArgumentException("Parameter must be 32 bytes long.");
        else this.bytes = bytes;

        //check if it has the long file name attribute.
        if(bytes[11] == FileAttribute.LONG_FILE_NAME.byteValue) this.isLfn = true;
        else this.isLfn = false;
    }


    public byte getAttribute() {
        return bytes[11];
    }
}
