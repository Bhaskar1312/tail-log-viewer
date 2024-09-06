package com.example.taillogviewer;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

@Service
public class LogTailService {

    private final Path logFilePath = Paths.get("tmp", "temp.txt");

    // public void tailFile(LogUpdateCallback callback) throws IOException {
    //     try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
    //         Path parentPath = logFilePath.getParent();
    //         if (parentPath != null) {
    //             parentPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
    //         }
    //
    //         while (true) {
    //             WatchKey key = watchService.take(); // Block until an event is available
    //             for (WatchEvent<?> event : key.pollEvents()) {
    //                 if (event.context().toString().equals(logFilePath.getFileName().toString())) {
    //                     // Log file modified
    //                     String newLogData = new String(Files.readAllBytes(logFilePath));
    //                     System.out.println("newLogData = " + newLogData);
    //                     callback.onUpdate(newLogData); // Push the new log data
    //                 }
    //             }
    //             key.reset();
    //         }
    //     } catch (InterruptedException e) {
    //         Thread.currentThread().interrupt();
    //     }
    // }

    public void tailFile(LogUpdateCallback callback) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(logFilePath.toFile(), "r")) {
            long filePointer = file.length(); // Start reading from the end of the file

            while (true) {
                long fileLength = file.length();

                // If the file has grown (new data has been appended)
                if (fileLength > filePointer) {
                    file.seek(filePointer); // Move file pointer to last read position

                    String line;
                    while ((line = file.readLine()) != null) {
                        // Notify the callback of new content (converted to UTF-8)
                        String byteString = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        System.out.println("byteString = " + byteString);
                        callback.onUpdate(byteString);
                    }

                    filePointer = file.getFilePointer(); // Update pointer to the new end of the file
                }

                // Pause briefly to avoid busy-waiting
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public interface LogUpdateCallback {
        void onUpdate(String newLogData);
    }
}
