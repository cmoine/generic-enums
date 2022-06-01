package org.cmoine.genericEnums;

@GenericEnum
public enum ComparableEnum {
    INT(int.class),
    STRING(String.class),
    INT2(int.class);

    ComparableEnum(Class<?> clazz) {

    }
}
