package com.cccz.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Test11 {

    private static final String STR = "helloworldabcdef";
    // 原子下标，线程安全
    private static final AtomicInteger index = new AtomicInteger(0);
    // 5个线程池
    private static final ExecutorService pool = Executors.newFixedThreadPool(5);

    public static void main(String[] args) {
        // 编排：t1 -> t2 -> t3 -> t4 -> t5 -> 循环
        Runnable t1 = () -> print();
        Runnable t2 = () -> print();
        Runnable t3 = () -> print();
        Runnable t4 = () -> print();
        Runnable t5 = () -> print();

        // 异步链式串行编排
        CompletableFuture.runAsync(t1, pool)
                .thenRunAsync(t2, pool)
                .thenRunAsync(t3, pool)
                .thenRunAsync(t4, pool)
                .thenRunAsync(t5, pool)
                .whenComplete((v, e) -> {
                    // 没打印完，递归接力，形成无限轮转
                    if (index.get() < STR.length()) {
                        main(new String[]{});
                    } else {
                        pool.shutdown();
                    }
                });
    }

    /**
     * 打印单个字符
     */
    private static void print() {
        int idx = index.getAndIncrement();
        if (idx < STR.length()) {
            System.out.print(STR.charAt(idx));
        }
    }
}
