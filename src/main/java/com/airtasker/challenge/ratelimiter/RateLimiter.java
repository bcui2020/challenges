package com.airtasker.challenge.ratelimiter;


import com.airtasker.challenge.ratelimiter.core.LazyRefillRateLimiter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateLimiter<T> {

  private static final Logger logger = LoggerFactory.getLogger(RateLimiter.class);

  private ConcurrentHashMap<T, LazyRefillRateLimiter> rateLimiterMap;

  private PriorityBlockingQueue<LazyRefillRateLimiter> rateLimitersQueue;

  private ScheduledExecutorService cleanUpExpiredPool;

  private int requestLimitPerHour;

  private static final int ONE_HOUR = 60 * 60 * 1000;

  private static final int TEN = 10;

  public RateLimiter(int requestLimitPerHour) {
    this.requestLimitPerHour = requestLimitPerHour;
    this.rateLimiterMap = new ConcurrentHashMap<>();
    this.rateLimitersQueue = new PriorityBlockingQueue<>();
    this.cleanUpExpiredPool = new ScheduledThreadPoolExecutor(1);
    setupCleanUpProcess();
  }

  private void setupCleanUpProcess() {
    synchronized (RateLimiter.class) {
      cleanUpExpiredPool.scheduleAtFixedRate(() -> {
        long now = System.currentTimeMillis();
        logger.info("Start clean up process");
        while (true) {
          LazyRefillRateLimiter keyInfo = rateLimitersQueue.peek();
          if (keyInfo == null || keyInfo.getLastRefillTime() + ONE_HOUR > now) {
            return;
          }
          if (!keyInfo.getId().equals("")) {
            rateLimiterMap.remove(keyInfo.getId());
            LazyRefillRateLimiter deleted = rateLimitersQueue.poll();
            logger.info("Deleted id {} in the storage", deleted.getId());
          }
        }
      }, 1, TEN, TimeUnit.MINUTES );
    }
  }

  public boolean allowRequest(T identifier) {
    return getRateLimiter(identifier).allow();
  }

  public long getWaitTime(T identifier) {
    return getRateLimiter(identifier).waitTime();
  }

  private synchronized LazyRefillRateLimiter getRateLimiter(T identifier) {

    if (rateLimiterMap.containsKey(identifier)) {
      return rateLimiterMap.get(identifier);
    }

    LazyRefillRateLimiter<T> newLazyRefillRateLimiter = new LazyRefillRateLimiter<>(identifier, requestLimitPerHour);

    rateLimiterMap.put(identifier, newLazyRefillRateLimiter);
    rateLimitersQueue.add(newLazyRefillRateLimiter);
    return newLazyRefillRateLimiter;
  }
}
