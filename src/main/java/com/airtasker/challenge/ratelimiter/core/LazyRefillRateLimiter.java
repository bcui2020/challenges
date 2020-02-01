package com.airtasker.challenge.ratelimiter.core;

import java.time.Clock;

public class LazyRefillRateLimiter<T> implements Comparable<LazyRefillRateLimiter> {

  private T id;

  private int remainTokens;

  private int maxTokens;

  private long lastRefillTime;

  private static final int DEFAULT_TOKEN_PER_REQUEST = 1;

  private Clock clock;

  public LazyRefillRateLimiter(int maxTokens) {
    this.maxTokens = maxTokens;
    this.remainTokens = maxTokens;
    this.clock = Clock.systemDefaultZone();
    this.lastRefillTime = clock.millis();
  }

  public LazyRefillRateLimiter(T id, int maxTokens) {
    this.id = id;
    this.maxTokens = maxTokens;
    this.remainTokens = maxTokens;
    this.clock = Clock.systemDefaultZone();
    this.lastRefillTime = clock.millis();
  }

  public long getLastRefillTime() {
    return lastRefillTime;
  }

  public T getId() {
    return id;
  }

  public LazyRefillRateLimiter(int maxTokens, Clock clock) {
    this.maxTokens = maxTokens;
    this.remainTokens = maxTokens;
    this.clock = clock;
    this.lastRefillTime = clock.millis();
  }

  public boolean allow() {
    return allow(DEFAULT_TOKEN_PER_REQUEST);
  }

  public boolean allow(int tokenNeeded) {
    synchronized (this) {
      refillTokens();
      if (remainTokens <= 0 || remainTokens < tokenNeeded) {
        return false;
      }
      remainTokens = remainTokens - tokenNeeded;
      return true;
    }
  }

  private void refillTokens() {

    long currentTime = clock.millis();
    double durationSinceLastRefill = (currentTime - lastRefillTime) / 1000.0;

    int cnt = (int) (durationSinceLastRefill / 3600 * maxTokens);

    if (cnt > 0) {
      remainTokens = Math.min(remainTokens + cnt, maxTokens);
      lastRefillTime = currentTime;
    }
  }

  public long waitTime() {
    return waitTime(DEFAULT_TOKEN_PER_REQUEST);
  }

  public long waitTime(int tokensNeeded) {
    if (remainTokens >= tokensNeeded) {
      return 0;
    }
    long singleTokenWaitTime = 60 * 60 * 1000 / maxTokens;

    long currentTime = clock.millis();

    long durationSinceLastRefill = (currentTime - lastRefillTime);

    return singleTokenWaitTime * (tokensNeeded - remainTokens) - durationSinceLastRefill;
  }

  @Override
  public int compareTo(LazyRefillRateLimiter o) {
    long timeDifference = this.lastRefillTime - o.lastRefillTime;

    if (timeDifference > 0) {
      return 1;
    }
    if (timeDifference < 0) {
      return -1;
    }
    return 0;
  }
}