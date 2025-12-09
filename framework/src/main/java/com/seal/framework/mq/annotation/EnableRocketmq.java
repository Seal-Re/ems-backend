package com.seal.framework.mq.annotation;

import com.seal.framework.mq.config.MQConfig;
import com.seal.framework.mq.config.MQTransactionListener;
import com.seal.framework.mq.handler.impl.DefaultMQHandler;
import com.seal.framework.mq.handler.impl.DefaultMQTransactionHandler;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import({MQConfig.class,
        MQTransactionListener.class,
        DefaultMQHandler.class,
        DefaultMQTransactionHandler.class})
public @interface EnableRocketmq {
}