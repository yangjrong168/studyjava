package org.yjr.spring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("test")
@Slf4j
public class TestController {
	 @GetMapping("sayHello")
      public String sayHello() {
    	  log.info("wwww...............");
    	  System.out.println("nihao");
    	  return "hello";
      }
}
