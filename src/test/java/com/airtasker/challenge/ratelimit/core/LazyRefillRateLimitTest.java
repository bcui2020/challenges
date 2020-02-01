package com.airtasker.challenge.ratelimit.core;

import static org.hamcrest.MatcherAssert.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;


class LazyRefillRateLimiterTest {

  @Test
  public void allowWithParameterShouldReturnTrueWhenThereIsEnoughTokenLeft() {
    LazyRefillRateLimiter lazyRefillRateLimiter = new LazyRefillRateLimiter(2);
    assertThat(lazyRefillRateLimiter.allow(1), Is.is(true));
  }

  @Test
  public void allowWithParameterShouldReturnFalseWhenThereIsNotEnoughTokenLeft() {
    LazyRefillRateLimiter lazyRefillRateLimiter = new LazyRefillRateLimiter(2);
    assertThat(lazyRefillRateLimiter.allow(3), Is.is(false));
  }

  @Test
  public void allowWithParameterShouldReturnFalseWhenInitialTokenIsZero() {
    LazyRefillRateLimiter lazyRefillRateLimiter = new LazyRefillRateLimiter(0);
    assertThat(lazyRefillRateLimiter.allow(), Is.is(false));
  }

  @Test
  public void allowWithNoParameterShouldReturnTrueWhenThereIsEnoughTokenLeft() {
    LazyRefillRateLimiter lazyRefillRateLimiter = new LazyRefillRateLimiter(2);
    assertThat(lazyRefillRateLimiter.allow(), Is.is(true));
  }

  @Test
  public void waitTimeWithParameterShouldReturnZeroWhenThereIsEnoughToken() {
    LazyRefillRateLimiter lazyRefillRateLimiter = new LazyRefillRateLimiter(10);
    assertThat(lazyRefillRateLimiter.waitTime(1), Is.is(0L));
  }

  @Test
  public void waitTimeWithParameterShouldReturnWaitTimeWhenThereIsNotEnoughToken() {
    String instantExpected = "2020-01-31T10:15:30Z";
    Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
    LazyRefillRateLimiter lazyRefillRateLimiter = new LazyRefillRateLimiter(10, clock);
    assertThat(lazyRefillRateLimiter.waitTime(11), Is.is(360000L));
  }

  @Test
  public void waitTimeWithNoParameterShouldReturnWaitTimeWhenThereIsNotEnoughToken() {
    LazyRefillRateLimiter lazyRefillRateLimiter = new LazyRefillRateLimiter(1);
    assertThat(lazyRefillRateLimiter.waitTime(), Is.is(0L));
  }

}


