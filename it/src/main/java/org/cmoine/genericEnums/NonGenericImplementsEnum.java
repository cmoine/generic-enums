package org.cmoine.genericEnums;

@GenericEnum
public enum NonGenericImplementsEnum implements Runnable {
    INT(int.class);

    NonGenericImplementsEnum(Class<?> clazz) {
    }

    @Override
    public void run() {
    }
}
