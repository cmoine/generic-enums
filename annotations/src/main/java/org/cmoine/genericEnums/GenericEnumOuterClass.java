package org.cmoine.genericEnums;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify the name of the generated outer class.
 * <p>
 * If name is not specified, the original name is used with a "Ext" suffix.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface GenericEnumOuterClass {

    String name() default "%Ext";
}
