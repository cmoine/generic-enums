package org.cmoine.genericEnums;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify mappings between type argument names and their types.
 *
 * <pre>
 *   &#64;GenericEnumConstructorParams({
 *     &#64;GenericEnumConstructorParams(String.class)
 *     &#64;GenericEnumConstructorParams(int.class)
 *   })
 *   MyClass() {
 *     this(String.class, int.class);
 *   }
 * </pre>
 *
 * See {@link GenericEnumConstructorParam} for more details.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.CONSTRUCTOR})
public @interface GenericEnumConstructorParams {
    GenericEnumConstructorParam[] value() default {};
}
