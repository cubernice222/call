package org.cuber.call.annotation;

import java.lang.annotation.*;

/**
 * Created by cuber on 2017/7/10.
 */
@Target(ElementType.PARAMETER)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {
    String value();
}
