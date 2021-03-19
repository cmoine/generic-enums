package org.cmoine.genericEnums;

@GenericEnum
public enum AbstractEnum {
    ONE(int.class) {
        private final int value=1;

        @Override
        public Integer getValue() {
            return value;
        }
    },
    TWO(double.class) {
        @Override
        public Double getValue() {
            return 2.0;
        }
    };

    AbstractEnum(Class clazz) {
    }

    @GenericEnumParam("T")
    public abstract Object getValue();
}
