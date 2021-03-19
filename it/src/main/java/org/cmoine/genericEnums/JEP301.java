package org.cmoine.genericEnums;

//enum Argument<X> { // declares generic enum
//    STRING<String>(String.class),
//            INTEGER<Integer>(Integer.class), ... ;
//
//    Class<X> clazz;
//
//    Argument(Class<X> clazz) { this.clazz = clazz; }
//
//    Class<X> getClazz() { return clazz; }
//    }
//
//    Class<String> cs = Argument.STRING.getClazz(); //uses sharper typing of enum constant
@GenericEnum(name = "Argument")
public enum JEP301 {
    STRING(String.class),
    INTEGER(Integer.class);

    private final Class<?> clazz;

    JEP301(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
