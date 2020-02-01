package com.airtasker.challenge.interceptors;

import com.airtasker.challenge.ratelimit.RateLimit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class IPBasedRateLimiterInterceptor extends HandlerInterceptorAdapter {

  private static final Logger logger = LoggerFactory.getLogger(IPBasedRateLimiterInterceptor.class);

  private int requestPerHour;

  private RateLimit<String> ipBasedRateLimit;

  @Autowired
  public IPBasedRateLimiterInterceptor(@Value("${ratelimiter.ipbased.limit}") final int requestPerHour) {
    this.requestPerHour = requestPerHour;
    this.ipBasedRateLimit = new RateLimit<>(requestPerHour);
  }

  public IPBasedRateLimiterInterceptor(int requestPerHour, RateLimit<String> ipBasedRateLimit){
    this.requestPerHour = requestPerHour;
    this.ipBasedRateLimit = ipBasedRateLimit;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    String ipAddress = getClientIpAddress(request);

    logger.info("Receive request for userid: {}, {}, {}", ipAddress, request.getRequestURI(), request.getMethod());

    if (ipAddress == null) {
      return false;
    }

    boolean allowAccess = ipBasedRateLimit.allowRequest(ipAddress);

    if (!allowAccess) {
      long waitTime = ipBasedRateLimit.getWaitTime(ipAddress);

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
