package com.sz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
// 扫描mybatis mapper包路径
@MapperScan(basePackages="com.sz.mapper")
// 扫描 所有需要的包, 包含一些自用的工具类包 所在的路径
@ComponentScan(basePackages= {"com.sz", "org.n3r.idworker"})
//@ComponentScan(basePackages = {"com.sz"})
public class Application {
	
	@Bean
	public SpringUtil getSpingUtil() {
		return new SpringUtil();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
