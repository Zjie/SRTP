package edu.ustb.info10;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 * 
 */
public class App {
	final static Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String now = df.format(new Date());
		System.out.println(now);
	}
}
