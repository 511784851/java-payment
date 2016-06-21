package com.guzhi.pay.task;

import org.testng.annotations.Test;

import com.guzhi.pay.task.TaskThreadPool;

public class TaskThreadPoolTest {

    @Test
    public void test() throws InterruptedException {
        final TaskThreadPool ttp = new TaskThreadPool(20, "test.thread");
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            final int idx = i;
            if (i <= 20) {
                ttp.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000 + idx * 10);
                        } catch (InterruptedException e) {
                        }
                        System.out.println(Thread.currentThread().getName() + " iiiiiiiiiiiiiiiii = " + idx);
                    }
                });
            }
            // System.out.println(tpe.getActiveCount() + "--" +
            // tpe.getCorePoolSize() + "--" + tpe.getMaxPoolSize() + "--"
            // + tpe.getPoolSize() + ".." + tpe.getThreadPoolExecutor());
        }
    }
}
