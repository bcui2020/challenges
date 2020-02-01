package com.airtasker.challenge.interceptors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import com.airtasker.challenge.ratelimiter.RateLimiter;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class IPBasedRateLimiterInterceptorTest {

  private IPBasedRateLimiterInterceptor ipBasedRateLimiterInterceptor;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private RateLimiter<String> ipBasedRateLimiter;

  @BeforeEach
  public void setup() {
    this.request = new MockHttpServletRequest();
    this.response = new MockHttpServletResponse();
    this.ipBasedRateLimiter = mock(RateLimiter.class);
    this.ipBasedRateLimiterInterceptor = new IPBasedRateLimiterInterceptor(10, ipBasedRateLimiter);
  }

  @Test
  public void preHandleShouldReturnTrueWhenFirstIPDoesNotReachTheLimit() throws Exception{
    when(ipBasedRateLimiter.allowRequest("127.0.0.1")).thenReturn(true);
    assertThat(ipBasedRateLimiterInterceptor.preHandle(request, response,null), Is.is(true));
  }

  @Test
  public void preHandleShouldReturnFalseWhenFirstIPReachesTheLimit() throws Exception{
    when(ipBasedRateLimiter.allowRequest("127.0.0.1")).thenReturn(false);
    assertThat(ipBasedRateLimiterInterceptor.preHandle(request, response,null), Is.is(false));
  }

  @Test
  public void preHandleShouldReturnTrueForIPTwoWhenFirstIPReachesTheLimit() throws Exception {
    when(ipBasedRateLimiter.allowRequest("127.0.0.1")).thenReturn(false);
    when(ipBasedRateLimiter.allowRequest("127.0.0.2")).thenReturn(true);
    request.setRemoteAddr("127.0.0.2");
    assertThat(ipBasedRateLimiterInterceptor.preHandle(request, response,null), Is.is(true));
  }

  @Test
  public void getClientIpAddressShouldReturnIPInTheHeaderIfExistsInTheHeader() {
    request.addHeader("X-Forwarded-For", "127.0.0.1");
    assertThat(ipBasedRateLimiterInterceptor.getClientIpAddress(request), Is.is("127.0.0.1"));
  }

  @Test
  public void getClientIpAddressShouldReturnFirstIPInTheHeaderIfExistInTheHeader() {
    request.addHeader("X-Forwarded-For", "127.0.0.2,127.0.0.1");
    assertThat(ipBasedRateLimiterInterceptor.getClientIpAddress(request), Is.is("127.0.0.2"));
  }

  @Test
  public void getClientIpAddressShouldReturnIPInTheRequestIfNoIpInTheHeader() {
    request.setRemoteAddr("127.0.0.2");
    assertThat(ipBasedRateLimiterInterceptor.getClientIpAddress(request), Is.is("127.0.0.2"));
  }
}