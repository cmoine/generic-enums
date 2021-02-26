package org.cmoine.genericEnums;

@GenericEnum
enum Primitive {
    BYTE(byte.class, (byte)0),
    SHORT(short.class, (short)0),
    INT(int.class, 0),
    FLOAT(float.class, 0f),
    LONG(long.class, 0L),
    DOUBLE(double.class, 0d),
    CHAR(char.class, 'a'),
    BOOLEAN(boolean.class, false),
    BOOLEAN_OBJECT(Boolean.class, false),
    STRING(String.class, "");

    private final Class<?> boxClass;
    @GenericEnumParam
    private final Object defaultValue;

    Primitive(Class<?> boxClass, @GenericEnumParam Object defaultValue) {
       this.boxClass = boxClass;
       this.defaultValue = defaultValue;
    }

    @GenericEnumParam
    public Object getDefaultValue() {
        return defaultValue;
    }
}