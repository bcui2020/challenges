package com.airtasker.challenge.interceptors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import com.airtasker.challenge.ratelimiter.UserBasedRateLimiter;
import javax.servlet.http.Cookie;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class UserBasedRateLimitInterceptorTest {
  private UserBasedRateLimitInterceptor userBasedRateLimitInterceptor;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private UserBasedRateLimiter userBasedRateLimiter;

  @BeforeEach
  public void setup() {
    this.request = new MockHttpServletRequest();
    this.response = new MockHttpServletResponse();
    this.userBasedRateLimiter = mock(UserBasedRateLimiter.class);
    this.userBasedRateLimitInterceptor = new UserBasedRateLimitInterceptor(userBasedRateLimiter);
  }

  @Test
  public void preHandleShouldReturnTrueWhenFirstIPDoesNotReachTheLimit() throws Exception {
    when(userBasedRateLimiter.allowRequest("airtasker1")).thenReturn(true);
    request.setCookies(new Cookie("userId", "airtasker1"));
    assertThat(userBasedRateLimitInterceptor.preHandle(request, response, null), Is.is(true));
  }

  @Test
  public void preHandleShouldReturnFalseWhenFirstIPReachesTheLimit() throws Exception {
    when(userBasedRateLimiter.allowRequest("airtasker1")).thenReturn(false);
    request.setCookies(new Cookie("userId", "airtasker1"));
    assertThat(userBasedRateLimitInterceptor.preHandle(request, response, null), Is.is(false));
  }

  @Test
  public void preHandleShouldReturnTrueForIPTwoWhenFirstIPReachesTheLimit() throws Exception {
    when(userBasedRateLimiter.allowRequest("airtasker1")).thenReturn(false);
    when(userBasedRateLimiter.allowRequest("airtasker2")).thenReturn(true);
    request.setCookies(new Cookie("userId", "airtasker2"));
    assertThat(userBasedRateLimitInterceptor.preHandle(request, response, null), Is.is(true));
  }
}