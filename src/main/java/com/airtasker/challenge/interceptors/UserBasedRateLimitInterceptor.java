package com.airtasker.challenge.interceptors;

import com.airtasker.challenge.ratelimiter.UserBasedRateLimiter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class UserBasedRateLimitInterceptor extends HandlerInterceptorAdapter {

  private static final Logger logger = LoggerFactory.getLogger(IPBasedRateLimiterInterceptor.class);

  @Autowired
  private UserBasedRateLimiter userBasedRateLimiter;

  public UserBasedRateLimitInterceptor(UserBasedRateLimiter userBasedRateLimiter) {
    this.userBasedRateLimiter = userBasedRateLimiter;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    String userId = getUserFromRequest(request);

    logger.info("Receive request for userid: {}, ", userId);

    if (!isValidUser(userId)) {
      return false;
    }

    boolean allowAccess = userBasedRateLimiter.allowRequest(userId);

    if (!allowAccess) {
      long waitTime = userBasedRateLimiter.getWaitTime(userId);

      logger.info("Block request for user: {} for too many request", userId);

      response.sendError(
          HttpStatus.TOO_MANY_REQUESTS.value(),
          String.format("Rate limit exceeded. Try again in %d seconds", waitTime / 1000));
    }
    return allowAccess;
  }

  private String getUserFromRequest(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("userId")) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  private Boolean isValidUser(String userId) {
    return userId != null && userId.startsWith("airtasker");
  }
}
