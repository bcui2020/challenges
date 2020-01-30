package com.airtasker.challenge.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import javax.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
    properties = {
        "USER_BASED_LIMIT_PER_HOUR=2",
        "IP_BASED_LIMIT_PER_HOUR=2"
    }
)
public class IntegrationTest {
  @Autowired
  private MockMvc mvc;

  @Test
  public void getHeartbeat() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/diagnostic/status/heartbeat"))
        .andExpect(status().isOk());
  }

  @Test
  public void allowRequestForIpBasedRequestWithinLimit() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/ip-based").header("X-Forwarded-For", "127.0.0.1"))
        .andExpect(status().isOk());
  }

  @Test
  public void rejectRequestForIpBasedRequestOverLimit() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/ip-based").header("X-Forwarded-For", "127.0.0.2"));
    mvc.perform(MockMvcRequestBuilders.get("/ip-based").header("X-Forwarded-For", "127.0.0.2"));
    mvc.perform(MockMvcRequestBuilders.get("/ip-based").header("X-Forwarded-For", "127.0.0.2"))
        .andExpect(status().is(429));
  }

  @Test
  public void allowRequestForUserBasedRequestWithinLimit() throws Exception {
    Cookie cookie = new Cookie("userId","airtasker1");
    mvc.perform(MockMvcRequestBuilders.get("/user-based").cookie(cookie))
        .andExpect(status().isOk());
  }

  @Test
  public void rejectRequestForUserBasedRequestOverLimit() throws Exception {
    Cookie cookie = new Cookie("userId","airtasker2");
    mvc.perform(MockMvcRequestBuilders.get("/user-based").cookie(cookie));
    mvc.perform(MockMvcRequestBuilders.get("/user-based").cookie(cookie));
    mvc.perform(MockMvcRequestBuilders.get("/user-based").cookie(cookie))
        .andExpect(status().is(429));
  }


}
