package org.cuber.call.callee.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * Created by cuber on 2017/7/10.
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface Callee {
}
