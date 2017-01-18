package org.yangjie.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Override // 默认首页
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("/index.html");
	}
	
}
