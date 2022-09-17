package com.example.rabbitmq.three;

/**
 * 版权：(C) 版权所有 2000-2021 上海天好电子商务股份有限公司苏州分公司
 * <简述> 睡眠工具类
 * <详细描述> SleepUtils
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
public class SleepUtils {
    public static void sleep(int second) {
        try {
            Thread.sleep(1000 * second);
        } catch (InterruptedException _ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
