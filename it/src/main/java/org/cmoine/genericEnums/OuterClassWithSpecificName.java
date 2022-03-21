package org.cmoine.genericEnums;

@GenericEnumOuterClass(name="MyOuterClass")
public class OuterClassWithSpecificName {

    @GenericEnum(name="InnerEnum")
    public enum InnerEnum {
        ONE(int.class),
        TWO(String.class);

        InnerEnum(Class<?> clazz) {

        }

        @Override
        public String toString() {
            return ordinal() + ": " + name();
        }
    }
}
