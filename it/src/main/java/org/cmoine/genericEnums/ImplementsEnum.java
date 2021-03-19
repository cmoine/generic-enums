package org.cmoine.genericEnums;

import java.util.function.Function;

@GenericEnum
public enum ImplementsEnum implements Function<@GenericEnumParam("U") Object, @GenericEnumParam Object> {
    STRING_TO_INT(String.class, Integer.class) {
        @Override
        @GenericEnumParam("U")
        public Object apply(@GenericEnumParam Object o) {
            return Integer.parseInt((String)o);
        }
    },
    INT_TO_STRING(Integer.class, String.class) {
        @Override
        @GenericEnumParam("U")
        public Object apply(@GenericEnumParam Object o) {
            return Integer.toString((Integer)o);
        }
    };

    ImplementsEnum(Class<?> from, Class<?> to) {
    }

    @Override
    @GenericEnumParam
    public Object apply(@GenericEnumParam("U") Object o2) {
        throw new IllegalArgumentException();
    }
}
