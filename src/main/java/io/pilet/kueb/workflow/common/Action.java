package io.pilet.kueb.workflow.common;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Action {
    String name();
    String description() default "";
    String[] params() default {};
    String[] param_asis() default {};
}
