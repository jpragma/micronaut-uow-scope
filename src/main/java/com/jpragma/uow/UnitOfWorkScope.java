package com.jpragma.uow;

import io.micronaut.runtime.context.scope.ScopedProxy;

import java.lang.annotation.*;

@ScopedProxy
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface UnitOfWorkScope {
    String value();
}
