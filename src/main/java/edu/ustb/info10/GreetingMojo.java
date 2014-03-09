package edu.ustb.info10;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Mojo( name = "sayhi")
public class GreetingMojo extends AbstractMojo {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "spring.xml" });
		DispatcherWithBFS dw = (DispatcherWithBFS) context
				.getBean("dispatcher");
		dw.logger.info("hello world");
	}
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("hello world");
	}
}
