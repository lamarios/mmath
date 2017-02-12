package com.ftpix.mmath.caching;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by gz on 12-Feb-17.
 */
public class Main {

    public static void main(String... args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(CachingConfiguration.class);

        FighterCache cache = (FighterCache) context.getBean("fighterCache");


        System.out.println(cache.get("91ac5e08f4e176faea48f9168ecfabda383a76389738da19897f4bc6f97a1409"));
    }
}
