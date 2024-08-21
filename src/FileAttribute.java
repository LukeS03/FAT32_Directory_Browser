public enum FileAttribute {
    READONLY       ((byte) 1),
    HIDDEN         ((byte) 2),
    SYSTEM         ((byte) 4),
    VOLUME_ID      ((byte) 8),
    LONG_FILE_NAME ((byte) 15),
    DIRECTORY      ((byte) 16),
    ARCHIVE        ((byte) 32);

    public final byte byteValue;

    FileAttribute(byte inputByte) {
        this.byteValue = inputByte;
    }

    public byte getValue() {
        return this.byteValue;
    }
}
