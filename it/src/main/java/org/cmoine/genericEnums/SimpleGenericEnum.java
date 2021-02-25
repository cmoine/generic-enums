package org.cmoine.genericEnums;

@GenericEnum
public enum SimpleGenericEnum {
    ONE(int.class),
    TWO(String.class)
    ;

    SimpleGenericEnum(Class<?> clazz) {

    }
}
