/**
 * A long file name.
 *
 * See <a href="https://wiki.osdev.org/FAT#Long_File_Names">OSDev Wiki</a>
 */
public class LongFileName extends DirectoryEntry {

    public LongFileName(byte[] bytes) throws IllegalArgumentException {
        super(bytes);
    }

    public byte entryIndex() {return bytes[0];}

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

    public byte getShortFileNameChecksum() {return bytes[13];}
}
