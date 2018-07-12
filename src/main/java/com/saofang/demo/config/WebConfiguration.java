package com.saofang.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
	
   /* @Override
    public void addInterceptors(InterceptorRegistry registry) {
        this.addInterceptors(registry);
//        registry.addInterceptor(new TestInterceptor()).addPathPatterns("/**");
    }
*/
   /* @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        this.addViewControllers(registry);
        registry.addViewController("/").setViewName("/index");
    }*/

}