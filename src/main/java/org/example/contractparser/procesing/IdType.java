package org.example.contractparser.procesing;

public enum IdType {

    NEWID("newid"),
    OLDID("oldid");

    private final String type;

    IdType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
