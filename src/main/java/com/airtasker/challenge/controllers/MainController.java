package com.airtasker.challenge.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

  @RequestMapping(method = RequestMethod.GET, path = "/ip-based", produces = "application/json")
  @ResponseBody
  public String home() {
    return "ip based rate limiter";
  }

  @RequestMapping(method = RequestMethod.GET, path = "/user-based", produces = "application/json")
  @ResponseBody
  public String home2() {
    return "user based rate limiter";
  }

}
