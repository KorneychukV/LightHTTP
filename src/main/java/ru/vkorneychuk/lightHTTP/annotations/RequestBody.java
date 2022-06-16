package ru.vkorneychuk.lightHTTP.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
public @interface RequestBody{

    public String name() default "";

}
