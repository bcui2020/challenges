package com.airtasker.challenge.ratelimiter;

public interface RateLimiter<T> {
  boolean allowRequest(T value);
  long getWaitTime(T value);
}
