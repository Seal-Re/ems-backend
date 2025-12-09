package com.seal.framework.mq.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * 根据topic和tag区分不同消息处理类
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)//使得运行时可以反射获取注解
@Target({ElementType.TYPE})
@Service
public @interface MQHandlerActualizer {
    /**
     * 消息主题 topic
     */
    String topic() default "";

    /**
     * 消息标签,如果是该主题下所有的标签，使用“*”
     */
    String[] tags() default "*";

    /**
     * 备注
     **/
    String remark() default "";
}
