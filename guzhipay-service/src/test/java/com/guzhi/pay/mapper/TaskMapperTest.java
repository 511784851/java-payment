package com.guzhi.pay.mapper;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.guzhi.pay.domain.Task;
import com.guzhi.pay.mapper.TaskMapper;

@ContextConfiguration(locations = "classpath*:dao-context.xml")
public class TaskMapperTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private TaskMapper mapper;
    String appId = "30";
    String appOrderId = "";

    @BeforeTest
    public void setUp() {
        appOrderId = System.currentTimeMillis() + "";
    }

    @Test
    public void updateTaskToOccupied() {
        updateAppOrderId();
        clearTestData();
        createTask(Task.TYPE_QUERY);
        int result = mapper.updateTaskToOccupied(appId, appOrderId, Task.TYPE_QUERY, 1);
        Assert.assertEquals(1, result);
    }

    @Test
    public void updateTaskDelay() {
        updateAppOrderId();
        updateTaskToOccupied();
        int result = mapper.updateTaskDelay(appId, appOrderId, Task.TYPE_QUERY, new Date());
        Assert.assertEquals(1, result);
        Task task = mapper.get(appId, appOrderId, Task.TYPE_QUERY);
        Assert.assertEquals(Task.FLAG_IDLE, task.getFlag());
    }

    private void updateAppOrderId() {
        appOrderId = appOrderId + 1;
    }

    private void createTask(String type) {
        Task task = new Task(appId, appOrderId, type, "Zfb", null);
        mapper.createTask(task);
    }

    private void clearTestData() {
        mapper.deleteTask(appId, appOrderId, Task.TYPE_QUERY);
        mapper.deleteTask(appId, appOrderId, Task.TYPE_NOTIFY);
    }
}
