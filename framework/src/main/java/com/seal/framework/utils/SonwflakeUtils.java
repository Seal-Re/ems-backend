package com.seal.framework.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;

/**
 * 雪花算法，获取唯一id
 */

public class SonwflakeUtils {
    private final Snowflake snowflake;

    /**
     * 成员类，SnowFlakeUtil的实例对象的保存域
     */
    private static class IdGenHolder {
        private static final SonwflakeUtils instance = new SonwflakeUtils();
    }

    /**
     * 外部调用获取SnowFlakeUtil的实例对象，确保不可变
     */
    public static SonwflakeUtils get() {
        return IdGenHolder.instance;
    }

    /**初始化构造，无参构造有参函数，默认节点都是
     *      * 0
     */
    private SonwflakeUtils() {
        String ipv4 = NetUtil.getLocalhostStr();
        long ipLong = Long.parseLong(ipv4.replaceAll("\\.", ""));
        long workerId = Long.hashCode(ipLong) % 32;
        long datacenterId = 2L;
        snowflake = IdUtil.createSnowflake(workerId, datacenterId);
    }

    /**
     * 获取唯一id
     **/
    public synchronized long id(){
        return snowflake.nextId();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(SonwflakeUtils.get().id());
        }
    }
}
