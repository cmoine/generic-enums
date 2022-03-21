package org.cmoine.genericEnums;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify a mapping between a type argument name and its type. Used to specify types (instead of
 * constructor parameters).
 *
 * <p>
 *
 * <p>If <code>name</code> is not specified, <code>"T"</code> will be used, then <code>"U"</code>
 * etc.
 *
 * <pre>
 *   MyEnum {
 *     STRING,
 *     ANOTHER_STRING,
 *     INTEGER(int.class);
 *
 *     &#64;GenericEnumConstructorParam(name = "T", type = String.class)
 *     MyEnum() {
 *       this(String.class);
 *     }
 *
 *     MyEnum(Class&lt;?&gt; clazz) {
 *       ...
 *     }
 *
 *   ...
 *
 *   &#64;GenericEnumConstructorParam(name = "T", type = String.class)
 *   &#64;GenericEnumConstructorParam(name = "U", type = int.class)
 *   MyOtherEnum() {
 *     this(String.class, int.class);
 *   }
 * </pre>
 *
 * <p>Can be specified multiple times.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.CONSTRUCTOR})
@Repeatable(value = GenericEnumConstructorParams.class)
public @interface GenericEnumConstructorParam {
  String name() default "";

  Class<?> type();
}
