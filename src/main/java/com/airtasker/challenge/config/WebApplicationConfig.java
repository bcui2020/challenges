package com.airtasker.challenge.config;

import com.airtasker.challenge.interceptors.IPBasedRateLimiterInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebApplicationConfig implements WebMvcConfigurer {

  @Autowired
  private IPBasedRateLimiterInterceptor ipBasedRateLimiterInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(ipBasedRateLimiterInterceptor).addPathPatterns("/ip-based");
  }

}