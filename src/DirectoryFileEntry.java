/**
 * Represents a standard '8.3' file entry.
 * <br> See <a href="https://wiki.osdev.org/FAT#Standard_8.3_format">OSDev Wiki</a>
 */
public class DirectoryFileEntry extends DirectoryEntry {

    public DirectoryFileEntry(byte[] bytes) throws IllegalArgumentException {
        super(bytes);
    }

    public byte[] getShortFileName() {
        byteBuffer = new byte[8];
        System.arraycopy(bytes, 0, byteBuffer, 0, 8);
        return byteBuffer;
    }
    public byte[] getExtension() {
        byteBuffer = new byte[3];
        System.arraycopy(bytes, 8, byteBuffer, 0, 3);
        return byteBuffer;
    }

    public byte getCreationTenthSeconds() {
        return bytes[13];
    }
    public byte[] getCreationTime() {
        byteBuffer = new byte[2];
        System.arraycopy(bytes, 14, byteBuffer, 0, 2);
        return byteBuffer;
    }
    public byte[] getCreationDate() {
        byteBuffer = new byte[2];
        System.arraycopy(bytes, 16, byteBuffer, 0, 2);
        return byteBuffer;
    }
    public byte[] getAccessedDate() {
        byteBuffer = new byte[2];
        System.arraycopy(bytes, 18, byteBuffer, 0, 2);
        return byteBuffer;
    }
    public byte[] getHighSectors() {
        byteBuffer = new byte[2];
        System.arraycopy(bytes, 20, byteBuffer, 0, 2);
        return byteBuffer;
    }
    public byte[] getLowSectors() {
        byteBuffer = new byte[2];
        System.arraycopy(bytes, 26, byteBuffer, 0, 2);
        return byteBuffer;
    }
    public byte[] getFileSize() {
        byteBuffer = new byte[4];
        System.arraycopy(bytes, 28, byteBuffer, 0, 4);
        return byteBuffer;
    }
    public byte[] getModificationDate() {
        byteBuffer = new byte[2];
        System.arraycopy(bytes, 24, byteBuffer, 0, 2);
        return byteBuffer;
    }

    public byte[] getModificationTime() {
        byteBuffer = new byte[2];
        System.arraycopy(bytes, 22, byteBuffer, 0, 2);
        return byteBuffer;
    }
}
