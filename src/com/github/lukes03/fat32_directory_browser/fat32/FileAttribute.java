package com.github.lukes03.fat32_directory_browser.fat32;

public enum FileAttribute {
    READONLY       (((byte) 1), "READ_ONLY"),
    HIDDEN         (((byte) 2), "HIDDEN"),
    SYSTEM         (((byte) 4), "SYSTEM"),
    VOLUME_ID      (((byte) 8), "VOLUME_ID"),
    LONG_FILE_NAME (((byte) 15), "LONG_FILE_NAME"),
    DIRECTORY      (((byte) 16), "DIRECTORY"),
    ARCHIVE        (((byte) 32), "ARCHIVE");

    public final byte byteValue;
    public final String attributeName;

    FileAttribute(byte inputByte, String attributeName) {
        this.byteValue = inputByte;
        this.attributeName = attributeName;
    }

    public static FileAttribute fromByteValue(byte byteValue) {
        return switch (byteValue) {
            case 1 -> FileAttribute.READONLY;
            case 2 -> FileAttribute.HIDDEN;
            case 4 -> FileAttribute.SYSTEM;
            case 8 -> FileAttribute.VOLUME_ID;
            case 15 -> FileAttribute.LONG_FILE_NAME;
            case 16 -> FileAttribute.DIRECTORY;
            case 32 -> FileAttribute.ARCHIVE;
            default -> null;
        };
    }
}
