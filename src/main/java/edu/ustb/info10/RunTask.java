package edu.ustb.info10;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Mojo( name = "beginToCrawl" )
public class RunTask extends AbstractMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "spring.xml" });
		DispatcherWithBFS dw = (DispatcherWithBFS) context
				.getBean("dispatcher");
//		dw.run();
		getLog().info("hello world" + dw.numOfUser);
	}

}
