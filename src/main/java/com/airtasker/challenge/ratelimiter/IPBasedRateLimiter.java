package com.airtasker.challenge.ratelimiter;

import com.airtasker.challenge.ratelimiter.core.LazyRefillRateLimiter;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class IPBasedRateLimiter implements RateLimiter<String>{

  private ConcurrentHashMap<String, LazyRefillRateLimiter> ipBasedRateLimiter;

  @Value("${ratelimiter.ipbased.limit}")
  private int requestLimitPerHour;

  public IPBasedRateLimiter() {
    this.ipBasedRateLimiter = new ConcurrentHashMap<>();
  }

  public void setRequestLimitPerHour(int requestLimitPerHour) {
    this.requestLimitPerHour = requestLimitPerHour;
  }

  @Override
  public boolean allowRequest(String ipAddress) {
    return getRateLimiter(ipAddress).allow();
  }

  @Override
  public long getWaitTime(String ipAddress) {
    return getRateLimiter(ipAddress).waitTime();
  }

  private synchronized LazyRefillRateLimiter getRateLimiter(String ipAddress) {
    return ipBasedRateLimiter.computeIfAbsent(ipAddress, key -> new LazyRefillRateLimiter(requestLimitPerHour));
  }
}
