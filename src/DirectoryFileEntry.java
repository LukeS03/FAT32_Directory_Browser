/**
 * Represents a standard '8.3' file entry.
 *
 * See <a href="https://wiki.osdev.org/FAT#Standard_8.3_format">OSDev Wiki</a>
 */
public class DirectoryFileEntry extends DirectoryEntry {

    public DirectoryFileEntry(Byte[] bytes) throws IllegalArgumentException {
        super(bytes);
    }

    public Byte[] getShortFileName() {
        byteBuffer = new Byte[8];
        System.arraycopy(bytes, 0, byteBuffer, 0, 8);
        return byteBuffer;
    }
    public Byte[] getExtension() {
        byteBuffer = new Byte[3];
        System.arraycopy(bytes, 3, byteBuffer, 0, 3);
        return byteBuffer;
    }

    public Byte getCreationTenthSeconds() {
        return bytes[13];
    }
    public Byte[] getCreationTime() {
        byteBuffer = new Byte[2];
        System.arraycopy(bytes, 14, byteBuffer, 0, 2);
        return byteBuffer;
    }
    public Byte[] getCreationDate() {
        byteBuffer = new Byte[2];
        System.arraycopy(bytes, 16, byteBuffer, 0, 2);
        return byteBuffer;
    }
    public Byte[] getAccessedDate() {
        byteBuffer = new Byte[2];
        System.arraycopy(bytes, 18, byteBuffer, 0, 2);
        return byteBuffer;
    }
    public Byte[] getHighSectors() {
        byteBuffer = new Byte[2];
        System.arraycopy(bytes, 20, byteBuffer, 0, 2);
        return byteBuffer;
    }
    public Byte[] getLowSectors() {
        byteBuffer = new Byte[2];
        System.arraycopy(bytes, 26, byteBuffer, 0, 2);
        return byteBuffer;
    }
    public Byte[] getFileSize() {
        byteBuffer = new Byte[4];
        System.arraycopy(bytes, 28, byteBuffer, 0, 4);
        return byteBuffer;
    }
    public Byte[] getModificationDate() {
        byteBuffer = new Byte[2];
        System.arraycopy(bytes, 24, byteBuffer, 0, 2);
        return byteBuffer;
    }

    public Byte[] getModificationTime() {
        byteBuffer = new Byte[2];
        System.arraycopy(bytes, 22, byteBuffer, 0, 2);
        return byteBuffer;
    }
}
