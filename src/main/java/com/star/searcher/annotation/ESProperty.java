package com.star.searcher.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2018-06-05 23:45:00
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ESProperty {
    String value();

    SearchMethod method() default SearchMethod.EQUAL;

    enum SearchMethod {
        NULL,
        NOT_NULL,
        EQUAL,
        LIKE,
        GT,
        LT,
        GTE,
        LTE,
        ;
    }
}
