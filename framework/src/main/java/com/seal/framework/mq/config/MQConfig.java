package com.seal.framework.mq.config;

import cn.hutool.core.thread.ThreadUtil;
import com.seal.framework.mq.handler.MQHandler;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * MQ配置
 */

@Configuration
public class MQConfig {
    @Value("${spring.application.name}")
    private String groupName;//集群名称

    /**
     * 消息消费者
     */
    @Autowired
    private Map<String, MQHandler> mqHandlerMap;

    @Value("${rocketmq.consumer.namesrvAddr:127.0.0.1:9876}")
    private String cNamesrvAddr;                                // 消费者nameservice地址
    @Value("${rocketmq.consumer.consumeThreadMin:20}")
    private int consumeThreadMin;                               // 最小线程数
    @Value("${rocketmq.consumer.consumeThreadMax:64}")
    private int consumeThreadMax;                               // 最大线程数
    @Value("${rocketmq.consumer.topics:*~*}")
    private String topics;                                      // 消费者监听topic 多个分号隔开（topic~tag;topic~tag）,如果要多个tag，tag间用||分割
    @Value("${rocketmq.consumer.consumeMessageBatchMaxSize:1}")
    private int consumeMessageBatchMaxSize;                     // 一次消费消息的条数，默认为1条


    @Bean
    public DefaultMQPushConsumer getRocketMQConsumer() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(cNamesrvAddr);
        consumer.setConsumeThreadMin(consumeThreadMin);
        consumer.setConsumeThreadMax(consumeThreadMax);
        consumer.registerMessageListener(getMessageListenerConcurrently());

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);//从队列的最新位置开始消费(忽略之前的），CONSUME_FROM_FIRST_OFFSET从头开始

        consumer.setMessageModel(MessageModel.BROADCASTING);//BROADCASTING（广播模式） CLUSTERING（集群模式）(默认）

        consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);// 设置一次消费消息的条数，默认为1条


        try {
            if ("*".equals(topics.trim())) {
                // 如果配置为通配符，则订阅所有主题和标签
                consumer.subscribe("*", "*");
            } else {
                String[] topicTagsArr = topics.split(";");//分割出订阅的主题
                for (String topicTags : topicTagsArr) {
                    String[] topicTag = topicTags.split("~");//tag
                    consumer.subscribe(topicTag[0], topicTag[1]);//subscribe可以自动识别||
                    System.out.println(topicTag[0] +  topicTag[1]);
                }
            }
            consumer.start();
        }catch (Exception e){
            throw new Exception(e);
        }
        return consumer;
    }

    @Bean
    public MessageListenerConcurrently getMessageListenerConcurrently() {
        return new MQListenerConcurrently(mqHandlerMap);
    }

    /***************************消息生产者***************************/
    @Autowired
    private MQTransactionListener mqTransactionListener;        //事务消息监听器

    @Value("${rocketmq.producer.namesrvAddr:127.0.0.1:9876}")
    private String pNamesrvAddr;                                //生产者nameservice地址
    @Value("${rocketmq.producer.maxMessageSize:4096}")
    private Integer maxMessageSize ;                            //消息最大大小，默认4M
    @Value("${rocketmq.producer.sendMsgTimeout:30000}")
    private Integer sendMsgTimeout;                             //消息发送超时时间，默认3秒
    @Value("${rocketmq.producer.retryTimesWhenSendFailed:2}")
    private Integer retryTimesWhenSendFailed;                   //消息发送失败重试次数，默认2次

    private static final ExecutorService executor = ThreadUtil.newExecutor(32);//执行任务的线程池

    //普通消息生产者
    @Bean
    public DefaultMQProducer defaultMQProducer() {
        DefaultMQProducer producer = new DefaultMQProducer(this.groupName);
        producer.setNamesrvAddr(this.pNamesrvAddr);
        producer.setMaxMessageSize(this.maxMessageSize);
        producer.setSendMsgTimeout(this.sendMsgTimeout);
        producer.setRetryTimesWhenSendFailed(this.retryTimesWhenSendFailed);
        try {
            producer.start();
        } catch (MQClientException e) {
            System.out.println(e.getErrorMessage());
        }
        return producer;
    }

    //事务消息生产者
    @Bean
    public TransactionMQProducer transactionMQProducer() {
        TransactionMQProducer producer = new TransactionMQProducer("transaction_" + this.groupName);
        producer.setNamesrvAddr(this.pNamesrvAddr);
        producer.setMaxMessageSize(this.maxMessageSize);
        producer.setSendMsgTimeout(this.sendMsgTimeout);
        producer.setRetryTimesWhenSendFailed(this.retryTimesWhenSendFailed);

        //添加事务消息处理线程池
        producer.setExecutorService(executor);
        //添加事务消息监听
        producer.setTransactionListener(mqTransactionListener);
        try {
            producer.start();
        } catch (MQClientException e) {
            System.out.println(e.getErrorMessage());
        }
        return producer;
    }
}
