package com.airtasker.challenge.interceptors;

import com.airtasker.challenge.ratelimiter.IPBasedRateLimiter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class IPBasedRateLimiterInterceptor extends HandlerInterceptorAdapter {

  private static final Logger logger = LoggerFactory.getLogger(IPBasedRateLimiterInterceptor.class);

  @Autowired
  private IPBasedRateLimiter ipBasedRateLimiter;

  public IPBasedRateLimiterInterceptor(IPBasedRateLimiter ipBasedRateLimiter) {
    this.ipBasedRateLimiter = ipBasedRateLimiter;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    String ipAddress = getClientIpAddress(request);

    logger.info("Receive request from {}, ", ipAddress);

    if (ipAddress == null) {
      return false;
    }

    boolean allowAccess = ipBasedRateLimiter.allowRequest(ipAddress);

    if (!allowAccess) {
      long waitTime = ipBasedRateLimiter.getWaitTime(ipAddress);

      logger.info("Block request for {} for too many request", ipAddress);

      response.sendError(
          HttpStatus.TOO_MANY_REQUESTS.value(),
          String.format("Rate limit exceeded. Try again in %d seconds", waitTime / 1000));
    }
    return allowAccess;
  }

  public String getClientIpAddress(HttpServletRequest request) {
    String ipAddress = request.getHeader("X-Forwarded-For");
    if (ipAddress == null || "".equals(ipAddress)) {
      return request.getRemoteAddr();
    } else {
      return ipAddress.contains(",") ? ipAddress.split(",")[0] : ipAddress;
    }
  }
}