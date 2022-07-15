package org.cmoine.genericEnums;

@GenericEnum
public enum ProvidedToStringEnum {
    INT(int.class);

    ProvidedToStringEnum(Class<?> clazz) {

    }

    @Override
    public String toString() {
        return "toString(): " + name();
    }
}
