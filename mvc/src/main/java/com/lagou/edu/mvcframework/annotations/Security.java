package com.lagou.edu.mvcframework.annotations;


import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Security {

    String[] value() default "";
}

