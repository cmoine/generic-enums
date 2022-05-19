package org.cmoine.genericEnums;

@GenericEnum
public enum DefaultConstructorEnum {
  ZERO_PARAM(),
  ONE_PARAM(int.class),
  TWO_PARAM(int.class, 2);

  private final Class<?> clazz;
  private final int _default;

  DefaultConstructorEnum() {
    this(int.class, 0);
  }

  DefaultConstructorEnum(Class<?> clazz) {
    this(clazz, 1);
  }

  DefaultConstructorEnum(Class<?> clazz, int _default) {
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
