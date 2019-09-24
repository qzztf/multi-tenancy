package cn.sexycode.tenant.core;

import org.springframework.context.ApplicationContext;

/**
 * @author qinzaizhen
 */
public class SpringContextUtil {
    static ApplicationContext context;

    public static void setContext(ApplicationContext context) {
        SpringContextUtil.context = context;
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T getBean(String beanName) {
        return (T) context.getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> tClass) {
        return context.getBean(beanName, tClass);
    }


}
