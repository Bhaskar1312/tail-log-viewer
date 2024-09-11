package com.example.taillogviewer;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Controller
@EnableScheduling
public class ProcessStreamController {


    private SimpMessagingTemplate messagingTemplate;

    ProcessStreamController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedDelayString = "60", timeUnit = TimeUnit.MINUTES)  // Poll every 60 min
    public void streamProcessOutput() {
        try {
            System.out.println("Start of method" );
            // Run a process (e.g., `tail -f` on a file or any command)
            // Process process = Runtime.getRuntime().exec(new String[]{"C:\\Program Files\\Git\\usr\\bin\\tail.exe",  "-f",  "tmp/temp.txt"});

            ProcessBuilder ps = new ProcessBuilder("C:\\Program Files\\Git\\usr\\bin\\tail.exe",  "-f",  "tmp/temp.txt"); // this works too, but deprecated
            // ps.redirectErrorStream(true);
            Process process = ps.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                // Send the process output to the WebSocket topic
                messagingTemplate.convertAndSend("/topic/process-output", line);
                System.out.println("line = " + line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("End of while loop" );
    }
}
