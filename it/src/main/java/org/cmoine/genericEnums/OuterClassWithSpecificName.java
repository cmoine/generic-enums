package org.cmoine.genericEnums;

@GenericEnum(name="MyOuterClass")
public class OuterClassWithSpecificName {

    @GenericEnum(name="InnerEnum")
    public enum InnerEnum {
        ONE(int.class),
        TWO(String.class);

        InnerEnum(Class<?> clazz) {

        }
    }
}