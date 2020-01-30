package com.airtasker.challenge.ratelimiter;


import com.airtasker.challenge.ratelimiter.core.LazyRefillRateLimiter;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class UserBasedRateLimiter implements RateLimiter<String> {

  private ConcurrentHashMap<String, LazyRefillRateLimiter> rateLimiterForUsers;

  @Value("${ratelimiter.userbased.limit}")
  private int requestLimitPerHour;

  public UserBasedRateLimiter() {
    this.rateLimiterForUsers = new ConcurrentHashMap<>();
  }

  public void setRequestLimitPerHour(int requestLimitPerHour) {
    this.requestLimitPerHour = requestLimitPerHour;
  }

  @Override
  public boolean allowRequest(String userId) {
    return getRateLimiter(userId).allow();
  }

  @Override
  public long getWaitTime(String userId) {
    return getRateLimiter(userId).waitTime();
  }

  private synchronized LazyRefillRateLimiter getRateLimiter(String userId) {
    return rateLimiterForUsers.computeIfAbsent(userId, key -> new LazyRefillRateLimiter(requestLimitPerHour));
  }

}
