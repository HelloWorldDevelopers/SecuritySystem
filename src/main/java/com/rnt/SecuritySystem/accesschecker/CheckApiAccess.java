package com.rnt.SecuritySystem.accesschecker;

 
import java.lang.annotation.*;

import org.springframework.http.HttpMethod;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckApiAccess {
    boolean apply() default true;
      
}
