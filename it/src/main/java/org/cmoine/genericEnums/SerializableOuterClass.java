package org.cmoine.genericEnums;

import java.io.Serializable;

public class SerializableOuterClass implements Serializable {
    private final static long serialVersionUID = 123L;

    @GenericEnum
    public enum InnerEnum {
        ONE(int.class),
        TWO(String.class);

        InnerEnum(Class<?> clazz) {

        }
    }
}
