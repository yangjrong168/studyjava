package org.yjr.designpatterns.structural.adapter.logexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Sl4jLog {
    
	private static final Logger log = LoggerFactory.getLogger(Sl4jLog.class);

	public void logPrint() {
		log.info("sl4jmsg");
		System.out.println("实现类是");

		System.out.println(log);
	}
}
