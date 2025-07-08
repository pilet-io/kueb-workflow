package io.pilet.kueb.common.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    String name();
    String pk() default "id";
    String update() default "";
    boolean cached() default false;
    String sort() default "";
    int limit() default 1000;
}
