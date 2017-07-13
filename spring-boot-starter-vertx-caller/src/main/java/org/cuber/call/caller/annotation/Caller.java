package org.cuber.call.caller.annotation;

import java.lang.annotation.*;

/**
 * Created by cuber on 2017/7/12.
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Caller {
    String value() default "";
}
