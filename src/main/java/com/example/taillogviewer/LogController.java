package com.example.taillogviewer;

import jakarta.annotation.PostConstruct;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
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
