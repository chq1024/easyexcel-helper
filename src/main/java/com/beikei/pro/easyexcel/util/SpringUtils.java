package com.beikei.pro.easyexcel.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringUtils implements ApplicationContextInitializer<ConfigurableApplicationContext>{

    private static ApplicationContext context = null;
    @Override
    public void initialize(ConfigurableApplicationContext  applicationContext) {
        context = applicationContext;
    }

    public static  <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T getBean(String name,Class<T> clazz) {
        return context.getBean(name,clazz);
    }



}
