package org.cmoine.genericEnums;

@GenericEnum
public enum PolymorphicConstructorEnum {
    ONE_PARAM(int.class),
    TWO_PARAM(int.class, 1);

    private final Class<?> clazz;
    private final int _default;

    PolymorphicConstructorEnum(Class<?> clazz) {
        this(clazz, 0);
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
