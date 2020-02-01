package com.airtasker.challenge.ratelimit.core;

public interface RateLimiter {
  boolean allow();
  long waitTime();
}
