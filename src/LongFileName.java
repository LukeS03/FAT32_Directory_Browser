/**
 * A long file name.
 *
 * See <a href="https://wiki.osdev.org/FAT#Long_File_Names">OSDev Wiki</a>
 */
public class LongFileName extends DirectoryEntry {

    public LongFileName(byte[] bytes) throws IllegalArgumentException {
        super(bytes);
    }

    public byte entryIndex() {return 0;}
    public byte[] getLongFileNameBytes() {return null;}
    public byte getShortFileNameChecksum() {return 0;}
}
