package com.example.taillogviewer;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class MyController {

    @GetMapping("/hello")
    public String myEndpoint() {
        return "Hello World!";
    }
}
