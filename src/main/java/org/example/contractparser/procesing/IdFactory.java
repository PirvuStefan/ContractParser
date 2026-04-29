package org.example.contractparser.procesing;

public class IdFactory {

    public static UserMapParser createIdParser(IdType idType) {
        if (idType.equals(IdType.NEWID)) {
            return new NewId();
        }
        else if (idType.equals(IdType.OLDID)) {
            return new OldId();
        }
        throw new IllegalArgumentException("Unsupported ID type: " + idType);
    }
}
