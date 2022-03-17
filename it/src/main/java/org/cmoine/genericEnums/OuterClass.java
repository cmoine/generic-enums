package org.cmoine.genericEnums;

public class OuterClass {

    @GenericEnum
    public enum InnerEnum {
        ONE(int.class),
        TWO(String.class);

        InnerEnum(Class<?> clazz) {

        }
    }
}
