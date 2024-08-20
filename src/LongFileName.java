/**
 * A long file name.
 *
 * See <a href="https://wiki.osdev.org/FAT#Long_File_Names">OSDev Wiki</a>
 */
public class LongFileName extends DirectoryEntry {

    public LongFileName(Byte[] bytes) throws IllegalArgumentException {
        super(bytes);
    }

    public Byte entryIndex() {return null;}
    public Byte[] getLongFileNameBytes() {return null;}
    public Byte getShortFileNameChecksum() {return null;}
}
