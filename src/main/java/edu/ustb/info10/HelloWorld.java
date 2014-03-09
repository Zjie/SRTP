package edu.ustb.info10;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HelloWorld {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "spring.xml" });
		DispatcherWithBFS dw = (DispatcherWithBFS) context
				.getBean("dispatcher");
		dw.logger.info("hello world");
	}
}
