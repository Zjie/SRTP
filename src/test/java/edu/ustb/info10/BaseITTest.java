package edu.ustb.info10;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;;

@ContextConfiguration(locations = { "classpath:spring.xml" })
public class BaseITTest extends AbstractJUnit4SpringContextTests  {
	
	@Before
	public void init() {
		assertNotNull(applicationContext);
	}
	@Test
	public void testContext() {
		assertNotNull(applicationContext);
	}
}