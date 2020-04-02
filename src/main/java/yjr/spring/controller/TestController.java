package yjr.spring.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("test")
@Slf4j
public class TestController {
	@Value("${timeout:100}")
	  private int timeout;
	@Value("${TEST1.people.name.style.time:2}")
	private int time;
	 @GetMapping("sayHello")
      public String sayHello() {
    	  log.info("wwww...............");
    	  System.out.println("nihao");
    	  return "hello timeout  "+timeout+"   time="+time;
      }
}
