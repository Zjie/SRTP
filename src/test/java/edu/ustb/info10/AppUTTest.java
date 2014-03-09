package edu.ustb.info10;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppUTTest extends TestCase {
	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public AppUTTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppUTTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		assertTrue(true);
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		final Log l1 = new Log(1);
		final Log l2 = new Log(2);
		l2.resume();
		l1.start();

		int j = 0;
		while (true) {
			j++;
			try {
				Thread.sleep(5000);
				l1.resume();
				l2.resume();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (j == 2) {
				l1.stop();
				l2.stop();
				break;
			}
		}
	}
}

class Log extends Thread {
	int i;

	public Log(int i) {
		this.i = i;
	}

	@Override
	public void run() {
		int j = 0;
		while (true) {
			if (j == 5) {
				this.suspend();
				j = 0;
			}
			System.out.println(i + " " + j);
			j++;
		}
	}

}
