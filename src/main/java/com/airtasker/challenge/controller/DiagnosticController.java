package airtasker.challenge.ratelimiter.controllers;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiagnosticController {

  @RequestMapping(path = "/diagnostic/status/heartbeat", method = GET)
  public ResponseEntity<String> heartbeat() {
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }
}