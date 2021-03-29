package org.cmoine.genericEnums;

import java.util.function.Predicate;

@GenericEnum
public enum IsDefault implements Predicate<@GenericEnumParam Object> {
    INT(int.class) {
        @Override
        public boolean test(@GenericEnumParam Object o) {
            return Integer.valueOf(0).equals(o);
        }
    },STRING(String.class) {
        @Override
        public boolean test(@GenericEnumParam Object o) {
            return "".equals(o);
        }
    };

    IsDefault(Class<?> clazz) {
    }

    @Override
    public abstract boolean test(@GenericEnumParam Object o);
}
