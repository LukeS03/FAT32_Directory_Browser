/**
 * Parent class for DirectoryFileEntry and LongFileName
 * <br>This just stores a 32 byte long array representing a single entry in a DirectoryTable.
 *
 */
abstract class DirectoryEntry {
    byte[] bytes;
    byte[] byteBuffer;
    /**
     * Construct an entry from a directory entry table using an array of 32 bytes.
     * @param bytes A 32 byte-long array from which the directory entry table is to be constructed.
     * @throws IllegalArgumentException Exception thrown if the parameter 'bytes' is not a 32 byte long array.
     */
    public DirectoryEntry(byte[] bytes) throws IllegalArgumentException {
        if(bytes.length != 32) throw new IllegalArgumentException("Parameter must be 32 bytes long.");
        else this.bytes = bytes;
    }

    public byte getAttribute() {
        return bytes[11];
    }
}
