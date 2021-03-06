package com.airtasker.challenge.config;

import com.airtasker.challenge.interceptors.IPBasedRateLimiterInterceptor;
import com.airtasker.challenge.interceptors.UserBasedRateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebApplicationConfig implements WebMvcConfigurer {

  @Autowired
  private IPBasedRateLimiterInterceptor ipBasedRateLimiterInterceptor;

  @Autowired
  private UserBasedRateLimitInterceptor userBasedRateLimitInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(ipBasedRateLimiterInterceptor).addPathPatterns("/ip-based");
    registry.addInterceptor(userBasedRateLimitInterceptor).addPathPatterns("/user-based");

  }

}