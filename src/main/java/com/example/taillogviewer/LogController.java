package com.example.taillogviewer;

import jakarta.annotation.PostConstruct;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;

@Controller
@CrossOrigin(origins = "*", allowCredentials = "true")
// @CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class LogController {

    private SimpMessagingTemplate template;

    private LogTailService logTailService;

    public LogController(SimpMessagingTemplate template, LogTailService logTailService) {
        this.template = template;
        this.logTailService = logTailService;
    }

    @PostConstruct
    public void init() {
        new Thread(() -> {
            try {
                logTailService.tailFile(newData -> {
                    template.convertAndSend("/topic/logs", newData);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
