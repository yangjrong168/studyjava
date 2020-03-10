package org.yjr.designpatterns.structural.adapter.logexample;

import org.apache.log4j.PropertyConfigurator;

public class LogTest {
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//DOMConfigurator.configure("");
		PropertyConfigurator.configure("D:\\java\\study\\studyjava\\src\\main\\resources\\log4j11.properties");
       
		
		new Sl4jLog().logPrint();
	}

}
