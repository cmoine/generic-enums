package org.cmoine.genericEnums;

@GenericEnum
public enum PolymorphicConstructorEnum {
    ZERO_PARAM,
    ONE_PARAM(int.class),
    TWO_PARAM(int.class, 2);

    private final Class<?> clazz;
    private final int _default;

    @GenericEnumConstructorParam(type = int.class)
    PolymorphicConstructorEnum() {
        this(int.class, 0);
    }

    PolymorphicConstructorEnum(Class<?> clazz) {
        this(clazz, 1);
    }

    PolymorphicConstructorEnum(Class<?> clazz, int _default) {
        this.clazz = clazz;
        this._default = _default;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public int get_default() {
        return _default;
    }
}
