package com.blemobi.payment.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.blemobi.payment.model.Red;
import com.blemobi.payment.service.RedService;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ApplicationContext act = new ClassPathXmlApplicationContext("applicationContext.xml");

		RedService redService = (RedService) act.getBean("redService");
		Red red = redService.selectByKey("1");
		System.out.println(red.getAmount());
	}

}
