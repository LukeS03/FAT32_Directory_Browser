package com.github.lukes03.fat32_directory_browser.masterbootrecord;


public enum PartitionTableAttributes {
    ACTIVE((byte) 0b00000010, "Active"),
    INACTIVE((byte) 0b00000000, "Inactive");

    public final byte byteValue;
    public final String attributeName;

    PartitionTableAttributes(byte byteValue, String attributeName) {
        this.byteValue = byteValue;
        this.attributeName = attributeName;
    }

}
