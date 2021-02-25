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
@GenericEnum(name = "MyJEP301", genericTypeName = "U")
public enum JEP301 {
}
