package com.example.taillogviewer;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.*;

@Service
public class LogTailService {

    private final Path logFilePath = Paths.get("tmp", "temp.txt");

    public void tailFile(LogUpdateCallback callback) throws IOException {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Path parentPath = logFilePath.getParent();
            if (parentPath != null) {
                parentPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            }

            while (true) {
                WatchKey key = watchService.take(); // Block until an event is available
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.context().toString().equals(logFilePath.getFileName().toString())) {
                        // Log file modified
                        String newLogData = new String(Files.readAllBytes(logFilePath));
                        System.out.println("newLogData = " + newLogData);
                        callback.onUpdate(newLogData); // Push the new log data
                    }
                }
                key.reset();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public interface LogUpdateCallback {
        void onUpdate(String newLogData);
    }
}
